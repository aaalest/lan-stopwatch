package io.github.aaalest.lanstopwatch.tracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

import io.github.aaalest.lanstopwatch.core.utils.ToggleController
import io.github.aaalest.lanstopwatch.core.utils.fadingEdge
import io.github.aaalest.lanstopwatch.tracker.data.Tracker
import io.github.aaalest.lanstopwatch.tracker.data.TrackerWithEvents
import io.github.aaalest.lanstopwatch.tracker.data.TimeEvent
import io.github.aaalest.lanstopwatch.tracker.data.AppDatabase
import io.github.aaalest.lanstopwatch.tracker.domain.EventType


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
fun TrackerRunStatusIcon(
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
    TrackerCard(trackerWithEvents = TrackerWithEvents(tracker = Tracker(label = "Test asdfasdfasdfsadfasdfasdfker(la ker(la")), deviceId = "Some device")
}

@Composable
fun TrackerCard(
    trackerWithEvents: TrackerWithEvents,
    deviceId: String, modifier:
    Modifier = Modifier
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.getDatabase(context.applicationContext) }
    val dao = db.trackerDao()

    val tracker = trackerWithEvents.tracker
    val events = trackerWithEvents.events

    val trackerCardSettingToggle = rememberSaveable(saver = ToggleController.Saver) {
        ToggleController(false)
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
            // TODO: automatically pause other tracker's RESUME event
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
        // TODO: apply color based on tracker.color
        colors = if (isRunning) CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) else CardDefaults.cardColors(),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                tracker.label,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .weight(1f)
                    .fadingEdge()
            )

            Spacer(modifier = Modifier.width(16.dp))

            TrackerRunStatusIcon(
                isRunning
            )

            Spacer(modifier = Modifier.width(16.dp))

            TrackerSettingsIcon(
                onClick = {
                    trackerCardSettingToggle.toggle()
                },
                modifier = Modifier
//                        .align(Alignment.Center)
                    .size(40.dp)
//                        .height(16.dp)
//                        .width(24.dp),
            )
        } // Row
    } // Card

    if (trackerCardSettingToggle.isActive) {
        TrackerSettingDialog(tracker, trackerCardSettingToggle)
    }
}
