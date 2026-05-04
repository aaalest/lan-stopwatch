package io.github.aaalest.lanstopwatch.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters


class TrackerConverters {
    @TypeConverter
    fun fromEventType(value: EventType) = value.name

    @TypeConverter
    fun toEventType(value: String) = enumValueOf<EventType>(value)
}


@Database(
    entities = [Stopwatch::class, TimeEvent::class],
    version = 1
)
@TypeConverters(TrackerConverters::class) // Add this line
abstract class AppDatabase : RoomDatabase() {
    abstract fun stopwatchDao(): StopwatchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stopwatch_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
