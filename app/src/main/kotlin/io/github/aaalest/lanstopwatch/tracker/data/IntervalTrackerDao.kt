package io.github.aaalest.lanstopwatch.tracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IntervalTrackerDao {
    @Transaction // Necessary because Room runs two queries behind the scenes
    @Query("SELECT * FROM interval_trackers")
    fun getAllTrackers(): Flow<List<IntervalTracker>>

    @Transaction
    @Query("SELECT * FROM interval_trackers WHERE hidden = 0")
    fun getVisibleRecords(): Flow<List<IntervalRecord>>

    @Query("UPDATE time_intervals SET endMillis = :endMillis WHERE endMillis IS NULL")
    suspend fun stopActiveIntervals(endMillis: Long = System.currentTimeMillis())

    @Insert
    suspend fun insertTracker(stopwatch: IntervalTracker): Long // Returns the new ID

    @Transaction
    @Query("SELECT * FROM interval_trackers WHERE id = :id")
    suspend fun getTrackerById(id: String): IntervalTracker

    @Update
    suspend fun updateTracker(stopwatch: IntervalTracker): Int // Returns count of updated rows

    @Query("""
    SELECT * FROM time_intervals 
    WHERE (startMillis <= :to) 
    AND (endMillis >= :from OR endMillis IS NULL)
    ORDER BY startMillis ASC
    """)
    fun getIntervalsInTimeRange(from: Long, to: Long): Flow<List<TimeInterval>>

    @Insert
    suspend fun insertInterval(interval: TimeInterval): Long // Returns the new Event ID
}
