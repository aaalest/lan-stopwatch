package io.github.aaalest.lanstopwatch.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [IntervalTracker::class, TimeInterval::class],
    version = 1
)
//@TypeConverters(TrackerConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun intervalTrackerDao(): IntervalTrackerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
