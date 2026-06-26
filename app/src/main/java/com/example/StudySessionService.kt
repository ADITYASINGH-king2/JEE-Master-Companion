package com.example

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.example.data.db.AppDatabase
import com.example.data.db.ClaimedMilestones
import com.example.data.pref.SecurePreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * StudySessionService - Core background tracking service for JEE preparation study stopwatch sessions.
 * Promotes itself as a Foreground Service compliant with Android 14+ 'specialUse' policies.
 * Employs a Battery Drain Fix by throttling tick rates when the host application is in the background.
 */
class StudySessionService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var timerJob: Job? = null

    private var accumulatedTimeMs = 0L
    private var lastTickTimeMs = 0L
    private var isBound = false

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    inner class LocalBinder : Binder() {
        fun getService(): StudySessionService = this@StudySessionService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder {
        isBound = true
        updateTickRate()
        return binder
    }

    override fun onRebind(intent: Intent?) {
        isBound = true
        updateTickRate()
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        updateTickRate()
        return true // Guarantees onRebind is called during subsequent bindings
    }

    companion object {
        private const val NOTIFICATION_ID = 9001
        private const val CHANNEL_ID = "study_session_channel"

        private val _timerSeconds = MutableStateFlow(0)
        val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

        private val _isTimerRunning = MutableStateFlow(false)
        val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

        // 12 Hours in seconds = 43200 seconds
        const val MEDAL_THRESHOLD_SECONDS = 43200

        const val ACTION_START = "com.example.action.START"
        const val ACTION_STOP = "com.example.action.STOP"
        const val ACTION_RESET = "com.example.action.RESET"

        fun startService(context: Context) {
            val intent = Intent(context, StudySessionService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, StudySessionService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }

        fun resetTimer(context: Context) {
            val intent = Intent(context, StudySessionService::class.java).apply {
                action = ACTION_RESET
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                if (!_isTimerRunning.value) {
                    startTimer()
                }
            }
            ACTION_STOP -> {
                stopTimer()
            }
            ACTION_RESET -> {
                resetTimerState()
            }
        }
        return START_STICKY
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        lastTickTimeMs = SystemClock.elapsedRealtime()
        
        // Promote to foreground under Android 14+ specialUse type
        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        updateTickRate()
    }

    private fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun resetTimerState() {
        accumulatedTimeMs = 0L
        _timerSeconds.value = 0
        lastTickTimeMs = SystemClock.elapsedRealtime()
        updateNotification()
    }

    private fun updateTickRate() {
        timerJob?.cancel()
        if (!_isTimerRunning.value) return

        // Battery Drain Fix: Throttles background timer to tick every 10 seconds (10000ms)
        // when unbound (app in background), reducing CPU wake-lock redraw triggers.
        // Ticks every 1 second (1000ms) when active UI is bound (foreground).
        val tickIntervalMs = if (isBound) 1000L else 10000L

        timerJob = serviceScope.launch {
            while (true) {
                delay(tickIntervalMs)
                tick()
            }
        }
    }

    private fun tick() {
        val now = SystemClock.elapsedRealtime()
        val delta = now - lastTickTimeMs
        lastTickTimeMs = now
        accumulatedTimeMs += delta
        
        val newSeconds = (accumulatedTimeMs / 1000).toInt()
        val oldSeconds = _timerSeconds.value
        _timerSeconds.value = newSeconds

        // Update notification on intervals to conserve system resource usage
        if (isBound || newSeconds % 10 == 0 || newSeconds == oldSeconds) {
            updateNotification()
        }

        // Medal award trigger when continuous active session hits 12 hours (43,200 seconds)
        if (newSeconds >= MEDAL_THRESHOLD_SECONDS && oldSeconds < MEDAL_THRESHOLD_SECONDS) {
            award12HourMedal()
        }
    }

    private fun award12HourMedal() {
        serviceScope.launch {
            withContext(Dispatchers.IO) {
                // Award repeatable '12 Hours a Day' medal
                val db = AppDatabase.getDatabase(applicationContext)
                val badgeId = "study_12_hours"
                val existing = db.claimedMilestonesDao().getMilestoneByBadgeId(badgeId)
                val count = (existing?.streakCount ?: 0) + 1
                
                db.claimedMilestonesDao().insertMilestone(
                    ClaimedMilestones(
                        badgeId = badgeId,
                        streakCount = count,
                        timestamp = System.currentTimeMillis()
                    )
                )

                // Grant reward XP (+1000 XP)
                val securePrefs = SecurePreferenceManager.getInstance(applicationContext)
                val currentXp = securePrefs.getTotalXp()
                securePrefs.setTotalXp(currentXp + 1000)
            }

            // Trigger broadcast
            val broadcastIntent = Intent("com.example.action.MEDAL_AWARDED").apply {
                putExtra("badge_id", "study_12_hours")
                putExtra("xp_granted", 1000)
            }
            sendBroadcast(broadcastIntent)

            // Post High-Priority Medal notification
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val awardNotification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle("🏆 Medal Unlocked!")
                .setContentText("You completed '12 Hours a Day' study session! +1000 XP granted.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()
            manager.notify(9002, awardNotification)
        }
    }

    private fun createNotification(): Notification {
        val secondsTotal = _timerSeconds.value
        val hours = secondsTotal / 3600
        val minutes = (secondsTotal % 3600) / 60
        val seconds = secondsTotal % 60
        val timeStr = String.format("%02d:%02d:%02d", hours, minutes, seconds)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Study Session Active")
            .setContentText("Elapsed Time: $timeStr")
            .setSmallIcon(android.R.drawable.star_on)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun updateNotification() {
        if (_isTimerRunning.value) {
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Study Stopwatch Session",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows the active JEE study session elapsed stopwatch"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        timerJob?.cancel()
        super.onDestroy()
    }
}
