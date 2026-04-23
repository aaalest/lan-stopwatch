package io.github.aaalest.lanstopwatch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface StopwatchDao {
    @Query("SELECT * FROM stopwatches")
    fun getAllStopwatches(): Flow<List<Stopwatch>>

    @Upsert
    suspend fun upsertStopwatch(stopwatch: Stopwatch)

    @Transaction
    @Query("SELECT * FROM stopwatches WHERE id = :id")
    suspend fun getStopwatchById(id: Long): Stopwatch?

    @Update
    suspend fun updateStopwatch(stopwatch: Stopwatch)

    @Delete
    suspend fun deleteStopwatch(stopwatch: Stopwatch)
}
