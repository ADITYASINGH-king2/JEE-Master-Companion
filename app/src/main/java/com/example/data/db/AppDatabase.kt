package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Lifecycle-Bound Database Class (Version 1).
 * Features a thread-safe singleton builder setup and fallback to destructive migrations.
 */
@Database(
    entities = [MockTestHistory::class, ClaimedMilestones::class, WeakChapter::class, StudyTask::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mockTestHistoryDao(): MockTestHistoryDao
    abstract fun claimedMilestonesDao(): ClaimedMilestonesDao
    abstract fun weakChapterDao(): WeakChapterDao
    abstract fun studyTaskDao(): StudyTaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Standard migration template showing how to execute safe SQL ALTER TABLE structures
         * for future updates without crashing on matching schema checks.
         * To activate this in future version increments, bump the version number to 2 in the @Database
         * annotation above, and append `.addMigrations(MIGRATION_1_2)` to the Room.databaseBuilder chain.
         */
        /*
        val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                // Example 1: Add a new nullable column to mock_test_history safely
                db.execSQL("ALTER TABLE mock_test_history ADD COLUMN subject_tag TEXT DEFAULT NULL")
                
                // Example 2: Add a new column to claimed_milestones with a secure default value
                db.execSQL("ALTER TABLE claimed_milestones ADD COLUMN badge_tier INTEGER NOT NULL DEFAULT 1")
            }
        }
        */

        /**
         * Thread-safe singleton builder setup.
         * Leverages synchronized block double-checking to guarantee single instance allocation.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jee_companion_database"
                )
                // Fallback to destructive migration serves as a safe lifecycle buffer during fast iterations
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
