package com.example

import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter

/**
 * CrashInterceptor - Intercepts fatal crashes on any JVM thread, serializes
 * the error stack trace, and redirects to MainActivity with diagnostic info.
 */
class CrashInterceptor(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            // Serialize stack trace to string
            val stringWriter = StringWriter()
            throwable.printStackTrace(PrintWriter(stringWriter))
            val stackTraceString = stringWriter.toString()

            Log.e("CrashInterceptor", "FATAL UNCAUGHT EXCEPTION INTERCEPTED:\n$stackTraceString")

            // Fire Recovery Intent back to MainActivity with a new task clear stack layout
            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("EXTRA_CRASH_INFO", stackTraceString)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            context.startActivity(intent)

            // Kill current process cleanly so that OS launches a fresh process immediately
            android.os.Process.killProcess(android.os.Process.myPid())
            java.lang.System.exit(10)
        } catch (e: Exception) {
            // Fallback to default JVM/Android crash dialogue if internal recovery mechanism errors
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        /**
         * Programmatically registers the UncaughtExceptionHandler global interceptor wrapper.
         */
        fun install(context: Context) {
            val handler = CrashInterceptor(context.applicationContext)
            Thread.setDefaultUncaughtExceptionHandler(handler)
        }
    }
}
