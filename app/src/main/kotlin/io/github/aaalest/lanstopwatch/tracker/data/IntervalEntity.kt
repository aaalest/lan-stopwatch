package io.github.aaalest.lanstopwatch.tracker.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

import io.github.aaalest.lanstopwatch.tracker.domain.TrackerColor


@Entity(
    tableName = "time_intervals",
    foreignKeys = [
        ForeignKey(
            entity = IntervalTracker::class,
            parentColumns = ["id"],
            childColumns = ["trackerId"],
            onDelete = ForeignKey.CASCADE // If tracker is deleted, delete its events too
        )
    ]
)
data class TimeInterval(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val trackerId: String,
    val startMillis: Long, // UTC
    val endMillis: Long?, // UTC
    val tzOffsetMillis: Double, // tz - time zone
    val deviceId: String
)

@Entity(tableName = "interval_trackers")
data class IntervalTracker(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    var label: String,
    var color: TrackerColor? = null,
    var hidden: Boolean = false
)

data class IntervalRecord(
    @Embedded val tracker: IntervalTracker,
    @Relation(
        parentColumn = "id",
        entityColumn = "trackerId"
    )
    val intervals: List<TimeInterval> = emptyList()
)
