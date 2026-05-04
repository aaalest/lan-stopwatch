package io.github.aaalest.lanstopwatch.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {
    @Transaction // Necessary because Room runs two queries behind the scenes
    @Query("SELECT * FROM trackers")
    fun getAllTrackers(): Flow<List<TrackerWithEvents>>

    @Insert
    suspend fun insertTracker(stopwatch: Tracker): Long // Returns the new ID

    @Transaction
    @Query("SELECT * FROM trackers WHERE id = :id")
    suspend fun getTrackerById(id: Long): Tracker?

    @Update
    suspend fun updateTracker(stopwatch: Tracker): Int // Returns count of updated rows

    @Delete
    suspend fun deleteTracker(stopwatch: Tracker): Int // Returns count of deleted rows

    @Insert
    suspend fun insertEvent(event: TimeEvent): Long // Returns the new Event ID

    @Query("DELETE FROM time_events WHERE trackerId = :stopwatchId")
    suspend fun deleteEventsForTracker(stopwatchId: Long): Int // Returns count of deleted events
}
