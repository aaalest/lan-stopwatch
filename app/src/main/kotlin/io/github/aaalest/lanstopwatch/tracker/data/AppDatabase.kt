package io.github.aaalest.lanstopwatch.tracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimeEventList(value: List<TimeEvent>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTimeEventList(value: String): List<TimeEvent> {
        val listType = object : TypeToken<List<TimeEvent>>() {}.type
        return gson.fromJson(value, listType)
    }
}


@Database(entities = [Stopwatch::class], version = 1)
@TypeConverters(Converters::class) // Add this line
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
