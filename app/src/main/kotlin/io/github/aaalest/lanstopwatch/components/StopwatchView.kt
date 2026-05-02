package io.github.aaalest.lanstopwatch.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.isEmpty
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

import io.github.aaalest.lanstopwatch.data.Stopwatch
import io.github.aaalest.lanstopwatch.data.TimeEvent
import io.github.aaalest.lanstopwatch.utils.VisibilityController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.text.append


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
fun StopwatchLabel(
    editedLabel: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = editedLabel,
        onValueChange = { newValue -> onValueChange(newValue) },
//                    keyboardOptions = KeyboardOptions(
//                        imeAction = ImeAction.Done // Changes the keyboard button to a Checkmark
//                    ),
//                    keyboardActions = KeyboardActions(
//                        onDone = {
//                            // 1. Hide the keyboard AND remove the cursor
//                            focusManager.clearFocus()
//
//                            // 2. Save your data to the database
//                            scope.launch {
//                                dao.upsertStopwatch(stopwatch.copy(label = editedLabel))
//                            }
//                        }
//                    ),
        textStyle = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        singleLine = true,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
        modifier = modifier
    )
}


@Composable
fun StopwatchTimeDisplay(
    elapsedSeconds: Long,
    modifier: Modifier = Modifier
) {
    Text(
        text = formatTime(elapsedSeconds),
        modifier = modifier
    )
}

@Composable
fun StopwatchResetTime(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset"
        )
    }
}


@Composable
fun StopwatchRunToggleButton(
    label: String,
    onClick: () -> Unit,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
//                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
        shape = shape,
        modifier = modifier
    ) {
        Text(label)
    }
}

@Preview(showBackground = true)
@Composable
fun StopwatchCardPreview() {
    StopwatchCard(stopwatch = Stopwatch(label = "New Stopwatch"), deviceId = "Some device")
}

@Composable
fun StopwatchCard(stopwatch: Stopwatch, deviceId: String) {
//    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.getDatabase(context.applicationContext) }
    val dao = db.stopwatchDao()

    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current

    var editedLabel by remember { mutableStateOf(stopwatch.label) }
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    var lastOrientation by rememberSaveable { mutableIntStateOf(configuration.orientation) }

    LaunchedEffect(isKeyboardOpen) {
        val isRotating = lastOrientation != configuration.orientation
        lastOrientation = configuration.orientation

        if (!isKeyboardOpen) {
            if (isRotating) {
//                println("IME: Hidden due to rotation")
            } else {
                focusManager.clearFocus()
//                println("IME: Hidden by User Gesture")
            }
        }
    }

    // Keep the local state in sync if the stopwatch object changes from the DB
//    LaunchedEffect(stopwatch.label) {
//        if (!isEditing) editedLabel = stopwatch.label
//    }

//    stopwatch.events = stopwatch.events.ifEmpty {
//        listOf(TimeEvent(EventType.START, System.currentTimeMillis(), deviceId))
//    } // If empty, add a START event
    var displayText by remember { mutableStateOf("") }
    var elapsedSeconds by remember { mutableLongStateOf(0L) }

    val isNew = stopwatch.events.isEmpty()
    var isRunning = stopwatch.events.lastOrNull()?.eventType == EventType.RESUME
//    var elapsedMillis = 0.0
    var pausedMillis = 0L

    if (isNew) {
        elapsedSeconds = 0
    } else {
        if (stopwatch.events.size > 2) {
            stopwatch.events.subList(1, stopwatch.events.size)
                .windowed(size = 2, step = 2) { (pause, resume) ->
                    pausedMillis += resume.timestamp - pause.timestamp
                }
        }

        LaunchedEffect(isRunning, stopwatch.events) {
            isRunning = stopwatch.events.lastOrNull()?.eventType == EventType.RESUME
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
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding((16).dp)
    ) {
        Column (
            modifier = Modifier
                .padding(16.dp)
//                .fillMaxWidth()
//                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.width(4.dp))

                StopwatchLabel(
                    editedLabel = editedLabel,
                    onValueChange = { newValue ->
                        editedLabel = newValue

                        // TODO: add a floating confirm and undo buttons instead of auto updating
                        // TODO: don't update empty editedLabel
                        // Update database on every change
//                        scope.launch {
//                            dao.upsertStopwatch(stopwatch.copy(label = newValue))
//                        }
                    },
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .align(Alignment.Bottom)
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { /* TODO: Toggle options */ },
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .align(Alignment.Top)
                        .height(16.dp)
                        .width(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Options",
//                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } // Row

            Spacer(modifier = Modifier.height(8.dp))

            Row (
                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier
//                    .fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(10.dp))

                StopwatchTimeDisplay(elapsedSeconds)

                Spacer(modifier = Modifier.weight(1f))

                StopwatchResetTime(
                    onClick = {
                        val updatedStopwatch = stopwatch.copy(events = emptyList())
                        scope.launch { dao.upsertStopwatch(updatedStopwatch) }
                    },
                    modifier = Modifier
                        .align(Alignment.Bottom)
                        .size(40.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                StopwatchRunToggleButton(
                    label = when {
                        isNew -> "Start"
                        isRunning -> "Pause"
                        else -> "Resume"
                    },
                    onClick = {
                        val newEvent = TimeEvent(
                            if (isRunning) EventType.PAUSE else EventType.RESUME,
                            System.currentTimeMillis(), deviceId
                        )

                        val updatedStopwatch = stopwatch.copy(
                            events = stopwatch.events + newEvent
                        )

                        scope.launch {
                            dao.upsertStopwatch(updatedStopwatch)
                        }
                    },
                    shape = RoundedCornerShape(if (isRunning) 16.dp else 32.dp),
                    modifier = Modifier
                        .height(64.dp) // Force a specific height
                        .defaultMinSize(minWidth = 120.dp) // Ensure it's wide enough
                )
            } // Row
        } // Column
    } // Card
}
