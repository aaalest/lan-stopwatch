package io.github.aaalest.lanstopwatch.tracker.data

import androidx.compose.ui.input.key.type
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class EventType {
    PAUSE,
    RESUME,
}

@Entity(
    tableName = "time_events",
    foreignKeys = [
        ForeignKey(
            entity = Stopwatch::class,
            parentColumns = ["id"],
            childColumns = ["stopwatchId"],
            onDelete = ForeignKey.CASCADE // If stopwatch is deleted, delete its events too
        )
    ]
)
data class TimeEvent(
    @PrimaryKey(autoGenerate = true) val eventId: Long = 0,
    val stopwatchId: Long, // The Foreign Key
    val eventType: EventType,
    val timestamp: Long,
    val deviceId: String
)

@Entity(tableName = "stopwatches")
data class Stopwatch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var label: String
)

data class StopwatchWithEvents(
    @Embedded val stopwatch: Stopwatch,
    @Relation(
        parentColumn = "id",
        entityColumn = "stopwatchId"
    )
    val events: List<TimeEvent> = emptyList()
)
