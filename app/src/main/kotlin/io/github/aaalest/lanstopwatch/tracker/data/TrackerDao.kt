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

    @Transaction
    @Query("SELECT * FROM trackers WHERE hidden = 0")
    fun getAllVisibleTrackers(): Flow<List<TrackerWithEvents>>

    @Transaction
    @Query("""
    SELECT * FROM trackers 
    WHERE id IN (
        SELECT trackerId FROM time_events as e1
        WHERE timestamp = (SELECT MAX(timestamp) FROM time_events as e2 WHERE e1.trackerId = e2.trackerId)
        AND eventType = 'RESUME'
    )
    LIMIT 1
    """)
    fun getFirstActiveTracker(): Flow<TrackerWithEvents?>

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
