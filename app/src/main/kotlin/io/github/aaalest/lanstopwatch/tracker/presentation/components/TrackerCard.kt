package io.github.aaalest.lanstopwatch.tracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import io.github.aaalest.lanstopwatch.tracker.data.AppDatabase
import io.github.aaalest.lanstopwatch.tracker.data.EventType

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

import io.github.aaalest.lanstopwatch.tracker.data.Tracker
import io.github.aaalest.lanstopwatch.tracker.data.TrackerWithEvents
import io.github.aaalest.lanstopwatch.tracker.data.TimeEvent
import kotlinx.coroutines.launch


@Composable
fun TrackerLabel(
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
fun TrackerResetTime(
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
fun TrackerSettingsIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
//        colors = IconButtonDefaults.filledIconButtonColors(
//            containerColor = MaterialTheme.colorScheme.primary,
//            contentColor = MaterialTheme.colorScheme.onPrimary
//        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings"
        )
    }
}


@Composable
fun TrackerRunToggleButton(
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


@Composable
fun TrackerRunToggleIcon(
    isRunning: Boolean,
//    shape: RoundedCornerShape,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
        contentDescription = "Run status indicator",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun TrackerCardPreview() {
    TrackerCard(trackerWithEvents = TrackerWithEvents(tracker = Tracker(label = "Test")), deviceId = "Some device")
}

@Composable
fun TrackerCard(trackerWithEvents: TrackerWithEvents, deviceId: String) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.getDatabase(context.applicationContext) }
    val dao = db.trackerDao()

    val tracker = trackerWithEvents.tracker
    val events = trackerWithEvents.events

    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current

    var editedLabel by remember { mutableStateOf(tracker.label) }
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


    val isRunning = events.lastOrNull()?.eventType == EventType.RESUME

    Card(
        onClick = {

            val newEvent = TimeEvent(
                trackerId = tracker.id,
                eventType = if (isRunning) EventType.PAUSE else EventType.RESUME,
                timestamp = System.currentTimeMillis(),
                deviceId = deviceId
            )

            scope.launch {
                dao.insertEvent(newEvent)
            }
        },
        shape = RoundedCornerShape(if (isRunning) 16.dp else 48.dp),
        colors = if (isRunning) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) else CardDefaults.cardColors(),
        modifier = Modifier
            .fillMaxWidth()
            .padding((16).dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.width(4.dp))

            TrackerLabel(
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
//                        .align(Alignment.Bottom)
            )

            Spacer(modifier = Modifier.weight(1f))

            TrackerRunToggleIcon(
                isRunning
            )

            Spacer(modifier = Modifier.width(16.dp))

            TrackerSettingsIcon(
                onClick = {
                    scope.launch { dao.deleteEventsForTracker(tracker.id) }
                },
                modifier = Modifier
//                        .align(Alignment.Center)
                    .size(40.dp)
//                        .height(16.dp)
//                        .width(24.dp),
            )
        } // Row
    } // Card
}
