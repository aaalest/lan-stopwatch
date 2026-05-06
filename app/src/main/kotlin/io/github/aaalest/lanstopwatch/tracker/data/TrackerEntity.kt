package io.github.aaalest.lanstopwatch.tracker.data

import androidx.compose.ui.input.key.type
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter

import io.github.aaalest.lanstopwatch.tracker.domain.EventType
import io.github.aaalest.lanstopwatch.tracker.domain.TrackerColor


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
    @PrimaryKey
    val eventId: String = java.util.UUID.randomUUID().toString(),
    val trackerId: String, // The Foreign Key
    val eventType: EventType,
    val timestamp: Long,
    val deviceId: String
)

@Entity(tableName = "trackers")
data class Tracker(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    var label: String,
    var color: TrackerColor? = null,
    var hidden: Boolean = false
)

data class TrackerWithEvents(
    @Embedded val tracker: Tracker,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackerId"
    )
    val events: List<TimeEvent> = emptyList()
)
