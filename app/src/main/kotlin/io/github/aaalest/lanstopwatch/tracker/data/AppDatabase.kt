package io.github.aaalest.lanstopwatch.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

import io.github.aaalest.lanstopwatch.tracker.domain.EventType


class TrackerConverters {
    @TypeConverter
    fun fromEventType(value: EventType) = value.name

    @TypeConverter
    fun toEventType(value: String) = enumValueOf<EventType>(value)
}


@Database(
    entities = [Tracker::class, TimeEvent::class],
    version = 1
)
@TypeConverters(TrackerConverters::class) // Add this line
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackerDao(): TrackerDao

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
