package io.github.aaalest.lanstopwatch.tracker.data

import androidx.compose.ui.input.key.type
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter

enum class EventType {
    PAUSE,
    RESUME,
}

@Entity(
    tableName = "time_events",
    foreignKeys = [
        ForeignKey(
            entity = Tracker::class,
            parentColumns = ["id"],
            childColumns = ["trackerId"],
            onDelete = ForeignKey.CASCADE // If tracker is deleted, delete its events too
        )
    ]
)
data class TimeEvent(
    @PrimaryKey(autoGenerate = true) val eventId: Long = 0,
    val trackerId: Long, // The Foreign Key
    val eventType: EventType,
    val timestamp: Long,
    val deviceId: String
)

@Entity(tableName = "trackers")
data class Tracker(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var label: String
)

data class TrackerWithEvents(
    @Embedded val tracker: Tracker,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackerId"
    )
    val events: List<TimeEvent> = emptyList()
)
