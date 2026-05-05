package io.github.aaalest.lanstopwatch.tracker.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.aaalest.lanstopwatch.tracker.data.AppDatabase
import java.time.ZonedDateTime
import java.time.LocalTime
import java.time.ZoneId

import io.github.aaalest.lanstopwatch.core.utils.mapRange

data class DayRange(val start: Long, val end: Long)

fun getTodayRange(): DayRange {
    val now = ZonedDateTime.now(ZoneId.systemDefault())

    val start = now.with(LocalTime.MIN).toInstant().toEpochMilli()
    val end = now.with(LocalTime.MAX).toInstant().toEpochMilli()
    return DayRange(start, end)
}

@Composable
private fun formatTime(totalSeconds: Long): AnnotatedString {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val numberStyle = SpanStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary
    )
    val unitStyle = SpanStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Gray
    )

    return buildAnnotatedString {
        // Hours
        withStyle(style = numberStyle) { append(String.format("%02d", hours)) }
        withStyle(style = unitStyle) { append("h ") }

        // Minutes
        withStyle(style = numberStyle) { append(String.format("%02d", minutes)) }
        withStyle(style = unitStyle) { append("m ") }

        // Seconds
        withStyle(style = numberStyle) { append(String.format("%02d", seconds)) }
        withStyle(style = unitStyle) { append("s") }
    }
}



@Composable
fun TrackerTimeDisplay(
    elapsedSeconds: Long,
    modifier: Modifier = Modifier
) {
    Text(
        text = formatTime(elapsedSeconds),
        modifier = modifier
    )
}


//fun calcDisplayTime(events: List<TimeEvent>): Long {
//    var displayText by remember { mutableStateOf("") }
//    var elapsedSeconds by remember { mutableLongStateOf(0L) }
//
//    val isNew = events.isEmpty()
//    var isRunning = events.lastOrNull()?.eventType == EventType.RESUME
////    var elapsedMillis = 0.0
//    var pausedMillis = 0L
//
//    if (isNew) {
//        elapsedSeconds = 0
//    } else {
//        if (events.size > 2) {
//            events.subList(1, events.size)
//                .windowed(size = 2, step = 2) { (pause, resume) ->
//                    pausedMillis += resume.timestamp - pause.timestamp
//                }
//        }
//
//        LaunchedEffect(isRunning, events) {
//            isRunning = events.lastOrNull()?.eventType == EventType.RESUME
//            if (isRunning) {
//                while (true) {
//                    val now = System.currentTimeMillis()
//                    val elapsedMillis = now - events[0].timestamp - pausedMillis
//                    elapsedSeconds = elapsedMillis / 1000
//                    println(displayText)
//                    kotlinx.coroutines.delay(1000 - (System.currentTimeMillis() % 1000))
//                }
//            } else {
//                val elapsedMillis = events.last().timestamp - events[0].timestamp - pausedMillis
//                elapsedSeconds = elapsedMillis / 1000
//            }
//        }
//    }
//    displayText = "${tracker.label}: ${elapsedSeconds}s; paused: ${pausedMillis / 1000}s; isRunning: $isRunning"
//}


data class ActivitySegment(
    val fromDegree: Float,
    val toDegree: Float,
    val color: Color
)

@Composable
fun ActivityCircle(
    segments: List<ActivitySegment>,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceContainerHigh

    Canvas(modifier = modifier.aspectRatio(1f)) {
        val strokeWidth = 12.dp.toPx()
        val radius = (size.minDimension / 2) - (strokeWidth / 2)
        val center = Offset(size.width / 2, size.height / 2)

        // 1. Draw the background circle (the "track")
        drawCircle(
            color = trackColor,
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        // 2. Draw each segment (the "lines")
        segments.forEach { segment ->
            val sweepAngle = if (segment.toDegree >= segment.fromDegree) {
                segment.toDegree - segment.fromDegree
            } else {
                (360f - segment.fromDegree) + segment.toDegree
            }

            drawArc(
                color = segment.color,
                startAngle = segment.fromDegree,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
    }
}

@Preview
@Composable
fun ActivityCartPreview() {
    ActivityCart()
}

@Composable
fun ActivityCart() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.getDatabase(context.applicationContext) }
    val dao = db.trackerDao()

    var segments: List<ActivitySegment> = listOf()
    val trackersWithEvents by dao.getAllTrackers().collectAsState(initial = emptyList())

    if (trackersWithEvents.isNotEmpty()) {
        trackersWithEvents.forEach { trackerWithEvents ->
            val events = trackerWithEvents.events
            val dayRange = getTodayRange()

            segments = events.chunked(2).map { pair ->
                val resumeTimestamp = pair[0].timestamp
                // If there is no second event (Pause), use the current time (Active segment)
                val pauseTimestamp = pair.getOrNull(1)?.timestamp ?: System.currentTimeMillis()

                ActivitySegment(
                    fromDegree = resumeTimestamp.mapRange(
                        dayRange.start, dayRange.end,
                        0, 360
                    ).toFloat(),
                    toDegree = pauseTimestamp.mapRange(
                        dayRange.start, dayRange.end,
                        0, 360
                    ).toFloat(),
                    color = Color.Red
                )
            }
        }
    }

    // TODO: Update the ActivityCircle every set time period
    ActivityCircle(
        segments,
        modifier = Modifier.padding(16.dp)
    )
}
