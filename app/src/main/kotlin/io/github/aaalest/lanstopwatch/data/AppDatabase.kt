package io.github.aaalest.lanstopwatch.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

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
