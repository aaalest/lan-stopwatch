package io.github.aaalest.lanstopwatch.tracker.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

import korlibs.math.geom.*
import korlibs.time.*

import io.github.aaalest.lanstopwatch.tracker.data.AppDatabase
import io.github.aaalest.lanstopwatch.tracker.data.IntervalTrackerDao
import io.github.aaalest.lanstopwatch.tracker.presentation.toComposeColor
import io.github.aaalest.lanstopwatch.core.utils.mapRange

data class DayRange(val start: Long, val end: Long)


/**
 * Returns the epoch timestamp (in milliseconds) for the start of the day (00:00:00)
 * corresponding to the given timestamp.
 */
fun getDayEpochFromTimestamp(timestampMillis: Long): Long {
    // 1. Convert the timestamp to a UTC DateTime object
    val dateTime = DateTime(timestampMillis)

    // 2. Get the start of the day (00:00:00.000) and convert it back to a Unix timestamp
    return dateTime.startOfDay.unixMillisLong
}

/**
 * Returns a DateTimeRange covering the entire day for a given timestamp.
 * The range is right-opened (inclusive of start, exclusive of end).
 */
fun getDayRangeFromTimestamp(timestampMillis: Long): DateTimeRange {
    // 1. Convert the timestamp to a UTC DateTime object
    val dateTime = DateTime(timestampMillis)

    // 2. Define the start and the exact end of the day
    val start = dateTime.startOfDay

    // 3. Create a right-opened range until the start of the next day
    // Alternatively, you could use `start until dateTime.dateDayEnd` depending on your inclusive/exclusive needs.
    return start until (start + 1.days)
}

//fun getDayEpochFromTimestamp(timestamp: Long): Long {
//    // Klock uses DateTime to represent a point in time
//    val date = DateTime.fromUnixMillis(timestamp)
//
//    // We want the number of days since 1970-01-01
//    // DateTime.unixDay is a built-in property in Klock!
//    return (date.unixMillis / 86400000.0).toLong()
//}
//
//fun getDayRange(timestamp: Long): DayRange {
//    val date = DateTime.fromUnixMillis(timestamp)
//
//    val start = DateTime.createUnchecked(
//        year = date.yearInt,
//        month = date.month1,
//        day = date.dayOfMonth,
//        hour = 0, minute = 0, second = 0
//    )
//
//    // Use the plus operator with Kotlin Duration for the next day
//    val end = start + 1.days
//
//    return DayRange(
//        start = start.unixMillisLong,
//        end = end.unixMillisLong
//    )
//}

fun separateLongEvents() {

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
    val id: String,
    val fromDegree: Float,
    val toDegree: Float,
    val color: Color
)

data class ActivityUiState(
    val segments: List<ActivitySegment> = emptyList(),
    val currentTimeInDegrees: Float = 0f
)

@Composable
fun ActivityCircle(
    activityUiState: ActivityUiState,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val strokeWidthDp = 12.dp

    // drawWithCache is vital when handling manual resize configChanges
    Spacer(
        modifier = modifier
            .aspectRatio(1f)
            .drawWithCache {
                val strokeWidth = strokeWidthDp.toPx()
                val radius = (size.minDimension / 2) - (strokeWidth / 2)
                val center = Offset(size.width / 2, size.height / 2)
                val arcSize = Size(radius * 2, radius * 2)
                val topLeft = Offset(center.x - radius, center.y - radius)

                val bounds = Rect(Offset.Zero, size)
                val layerPaint = Paint()

                onDrawBehind {
                    // 1. Draw track
                    drawCircle(
                        color = trackColor,
                        radius = radius,
                        style = Stroke(width = strokeWidth)
                    )

                    // 2. Draw segments
                    activityUiState.segments.forEach { segment ->
                        // Simplify sweep logic since ViewModel now handles the 'to >= from' fix
                        val sweepAngle = (segment.toDegree - segment.fromDegree).coerceAtLeast(0f)

                        if (sweepAngle < 0.1f) {
                            val startAngle = (segment.fromDegree - 90f).degrees
                            drawCircle(
                                color = segment.color,
                                radius = strokeWidth / 2,
                                center = Offset(
                                    x = center.x + radius * cosf(startAngle),
                                    y = center.y + radius * sinf(startAngle)
                                )
                            )
                        } else {
                            drawArc(
                                color = segment.color,
                                startAngle = segment.fromDegree - 90f, // Start at 12 o'clock
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                topLeft = topLeft,
                                size = arcSize
                            )
                        }
                    }

                    // Draw interlocking crescent to create visual padding between previous and next day
                    drawContext.canvas.saveLayer(bounds, layerPaint)

                    val demoStartDegree = activityUiState.currentTimeInDegrees
                    val demoSweep = 50f

                    rotate(degrees = demoStartDegree - 90f, pivot = center) {
                        drawIntoCanvas { canvas ->
                            val arcRect = Rect(topLeft, arcSize)
                            val arcPaint = Paint().apply {
                                isAntiAlias = true
                                style = PaintingStyle.Stroke
                                this.strokeWidth = strokeWidth
                                strokeCap = StrokeCap.Round

                                shader = SweepGradientShader(
                                    center = center,
                                    colors = listOf(
                                        backgroundColor.copy(alpha = 0.75f),
                                        backgroundColor.copy(alpha = 0f)
                                    ),
                                    colorStops = listOf(
                                        0f,                  // Fade starts exactly at the beginning of the arc
                                        (demoSweep / 360f)   // Fade hits 0% exactly at the end of the 5-degree sweep
                                    )
                                )
                            }
                            canvas.drawArc(
                                rect = arcRect,
                                startAngle = 0f,
                                sweepAngle = demoSweep,
                                useCenter = false,
                                paint = arcPaint
                            )

                            val clearPaint = Paint().apply {
                                isAntiAlias = false
                                blendMode = BlendMode.Clear
                                style = PaintingStyle.Stroke
                                this.strokeWidth = strokeWidth + 1.5f // Must match the width of the arc it's cutting
                                strokeCap = StrokeCap.Round
                            }
                            canvas.drawArc(
                                rect = arcRect,
                                startAngle = 0f,
                                sweepAngle = -5f,
                                useCenter = false,
                                paint = clearPaint
                            )
                        }
                    }

                    drawContext.canvas.restore()
                }
            }
    )
}

@Preview
@Composable
fun ActivityChartPreview() {
    ActivityChart(
        backgroundColor = MaterialTheme.colorScheme.background
    )
}

class ActivityViewModel(private val dao: IntervalTrackerDao) : ViewModel() {
    // 1. Define the time range for "Today"
    private val today = DateTime.now()
    private val dayRangeInMillis = today.startOfDay.unixMillisLong until today.endOfDay.unixMillisLong

    // 2. A ticker that emits the current time every second
    private val currentTimeFlow = flow {
        while (true) {
            emit(System.currentTimeMillis())
            delay(1000)
        }
    }

    // 3. The Main UI State
    // We combine Intervals, Trackers, and the Ticker into one stable stream
    val uiState: StateFlow<ActivityUiState> = combine(
        dao.getIntervalsInTimeRange(dayRangeInMillis.first, dayRangeInMillis.last),
        dao.getAllTrackers(),
        currentTimeFlow
    ) { intervals, trackers, currentTime ->
        val trackerMap = trackers.associateBy { it.id }

        val segments = intervals.mapNotNull { interval ->
            val tracker = trackerMap[interval.trackerId] ?: return@mapNotNull null

            // If interval is ongoing (end is null), use current ticker time
            val effectiveEndMillis = (interval.endMillis ?: currentTime)
                .coerceAtLeast(interval.startMillis)

            ActivitySegment(
                id = interval.id,
                fromDegree = mapMillisToDegree(interval.startMillis),
                toDegree = mapMillisToDegree(effectiveEndMillis),
                color = tracker.color?.toComposeColor() ?: Color.White
            )
        }

        ActivityUiState(
            segments = segments,
            currentTimeInDegrees = mapMillisToDegree(currentTime)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActivityUiState()
    )

    private fun mapMillisToDegree(millis: Long): Float {
        return millis.toDouble().mapRange(
            inMin = dayRangeInMillis.first.toDouble(),
            inMax = dayRangeInMillis.last.toDouble(),
            outMin = 0.0,
            outMax = 360.0
        ).toFloat()
    }

    // 4. The Factory (Required for manual dependency injection)
    companion object {
        fun Factory(dao: IntervalTrackerDao): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActivityViewModel(dao) as T
            }
        }
    }
}

@Composable
fun ActivityChart(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    // 1. Get your dependencies first
    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val dao = remember { db.intervalTrackerDao() }

    // 2. Initialize the ViewModel here
    // This is the ONLY place you should call this.
    // Sub-composables should just take the 'uiState' or the ViewModel as a parameter.
    val viewModel: ActivityViewModel = viewModel(
        factory = ActivityViewModel.Factory(dao)
    )

    // 3. Observe the state
    val activityUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 4. Pass the data down to drawing logic
    ActivityCircle(
        activityUiState = activityUiState,
        backgroundColor = backgroundColor,
        modifier
    )
}