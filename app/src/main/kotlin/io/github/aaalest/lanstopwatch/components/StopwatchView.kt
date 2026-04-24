package io.github.aaalest.lanstopwatch.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.aaalest.lanstopwatch.data.AppDatabase
import io.github.aaalest.lanstopwatch.data.EventType

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.ui.Alignment

import io.github.aaalest.lanstopwatch.data.Stopwatch
import io.github.aaalest.lanstopwatch.data.TimeEvent
import io.github.aaalest.lanstopwatch.utils.VisibilityController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


private fun formatTime(totalSeconds: Long): String {
    val days = totalSeconds / 86400
    val hours = (totalSeconds % 86400) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds)
}


@Composable
fun StopwatchCard(stopwatch: Stopwatch, deviceId: String) {
//    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.getDatabase(context.applicationContext) }
    val dao = db.stopwatchDao()

//    stopwatch.events = stopwatch.events.ifEmpty {
//        listOf(TimeEvent(EventType.START, System.currentTimeMillis(), deviceId))
//    } // If empty, add a START event
    var displayText by remember { mutableStateOf("") }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }

    val isNew = stopwatch.events.isEmpty()
    val isRunning = stopwatch.events.lastOrNull()?.eventType == EventType.RESUME
//    var elapsedMillis = 0.0
    var pausedMillis = 0L

    if (isNew) {
        elapsedSeconds = 0
    } else {
        if (stopwatch.events.size > 3) {
            stopwatch.events.subList(1, stopwatch.events.size)
                .windowed(size = 2, step = 2) { (pause, resume) ->
                    pausedMillis += resume.timestamp - pause.timestamp
                }
        }

        LaunchedEffect(isRunning, displayText) {
            if (isRunning) {
                while (true) {
                    val now = System.currentTimeMillis()
                    val elapsedMillis = now - stopwatch.events[0].timestamp - pausedMillis
                    elapsedSeconds = elapsedMillis / 1000
                    println(displayText)
                    kotlinx.coroutines.delay(1000 - (System.currentTimeMillis() % 1000))
                }
            } else {
                val elapsedMillis = stopwatch.events.last().timestamp - stopwatch.events[0].timestamp - pausedMillis
                elapsedSeconds = elapsedMillis / 1000
            }
        }
    }
    displayText = "${stopwatch.label}: ${elapsedSeconds}s; paused: ${pausedMillis / 1000}s; isRunning: $isRunning"

    Card(
        onClick = {
            // 1. Create the new event
            val newEvent = if (isRunning) {
                TimeEvent(EventType.PAUSE, System.currentTimeMillis(), deviceId)
            } else {
                TimeEvent(EventType.RESUME, System.currentTimeMillis(), deviceId)
            }

            // 2. Create a copy of the stopwatch with the NEW list
            val updatedStopwatch = stopwatch.copy(
                events = stopwatch.events + newEvent
            )

            // 3. Launch a coroutine to save to the database
            scope.launch {
                dao.upsertStopwatch(updatedStopwatch)
            }
        },
        shape = RoundedCornerShape(if (isRunning) 16.dp else 32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding((16).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
//                modifier = Modifier.weight(1f) // Takes up remaining space
            ) {
                Text(
                    text = stopwatch.label,
//                    style = MaterialTheme.typography.titleLarge,
//                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Pause Indicator",
                    )
                    Text(
                        text = formatTime(elapsedSeconds),
    //                    style = MaterialTheme.typography.headlineMedium,
    //                    fontWeight = FontWeight.ExtraBold,
    //                    color = if (isRunning) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                // Two Buttons on the Right
                Row {
                    IconButton(
                        onClick = {
                            // Reset Logic: Clear events and save
                            val updatedStopwatch = stopwatch.copy(events = emptyList())
                            scope.launch { dao.upsertStopwatch(updatedStopwatch) }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 0,
                            count = 2
                        ),
//                        selected = index == selectedIndex,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
//                            tint = MaterialTheme.colorScheme.error
                        )
                    }

                    IconButton(
                        onClick = {
                            // Reset Logic: Clear events and save
                            val updatedStopwatch = stopwatch.copy(events = emptyList())
                            scope.launch { dao.upsertStopwatch(updatedStopwatch) }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 1,
                            count = 2
                        ),
//                        selected = index == selectedIndex,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dehaze,
                            contentDescription = "Reset",
//                            tint = MaterialTheme.colorScheme.error
                        )
                    }
//                    Button(
//                        onClick = { /* Action 1 (e.g. Reset) */ },
//                        modifier = Modifier.padding(end = 4.dp)
//                    ) {
//                        Text("Reset")
//                    }
//                    Button(
//                        onClick = { /* Action 2 (already handled by Card onClick usually) */ }
//                    ) {
//                        Text(if (isRunning) "Pause" else "Resume")
//                    }
                }
            }
        }
    }
//        Text(
//            when {
//                isNew -> "Start"
//                isRunning -> "Pause"
//                else -> "Resume"
//            }
//        )
}

//@Composable
//fun StopwatchView(
//    stopwatch: Stopwatch,
//)

//@Composable
//fun StopwatchView(
//    card: Stopwatch,
//    isFlipped: VisibilityController,
//) {
//
//    Card(
//        onClick = { isFlipped.toggle() },
//        shape = RoundedCornerShape(32.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding((12 * 2).dp)
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center,
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            // Display Word or Definition based on state
//            val contentText = if (isFlipped.isVisible) card.definition else card.word
//
//            Text(
//                text = contentText,
//                style = MaterialTheme.typography.headlineMedium,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Optional: Show the other side subtly when flipped
//            AnimatedVisibility(visible = isFlipped.isVisible) {
//                Text(
//                    text = "Tap to see the word again.",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color.Gray
//                )
//            }
//        }
//    }
//}
