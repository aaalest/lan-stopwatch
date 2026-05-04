package io.github.aaalest.lanstopwatch.tracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StopwatchDao {
    @Transaction // Necessary because Room runs two queries behind the scenes
    @Query("SELECT * FROM stopwatches")
    fun getAllStopwatches(): Flow<List<StopwatchWithEvents>>

    @Insert
    suspend fun insertStopwatch(stopwatch: Stopwatch): Long // Returns the new ID

    @Transaction
    @Query("SELECT * FROM stopwatches WHERE id = :id")
    suspend fun getStopwatchById(id: Long): Stopwatch?

    @Update
    suspend fun updateStopwatch(stopwatch: Stopwatch): Int // Returns count of updated rows

    @Delete
    suspend fun deleteStopwatch(stopwatch: Stopwatch): Int // Returns count of deleted rows

    @Insert
    suspend fun insertEvent(event: TimeEvent): Long // Returns the new Event ID

    @Query("DELETE FROM time_events WHERE stopwatchId = :stopwatchId")
    suspend fun deleteEventsForStopwatch(stopwatchId: Long): Int // Returns count of deleted events
}
