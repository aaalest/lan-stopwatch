package io.github.aaalest.lanstopwatch.tracker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.aaalest.lanstopwatch.core.utils.ToggleController
import io.github.aaalest.lanstopwatch.core.utils.fadingEdge
import io.github.aaalest.lanstopwatch.tracker.data.AppDatabase
import io.github.aaalest.lanstopwatch.tracker.data.Tracker
import io.github.aaalest.lanstopwatch.tracker.domain.TrackerColor
import io.github.aaalest.lanstopwatch.tracker.presentation.toComposeColor
import kotlinx.coroutines.launch


@Composable
fun TrackerLabelTextField(
    editedLabel: String,
    onValueChange: (String) -> Unit,
    isKeyboardOpen: Boolean,  // keyboard status can't be correctly observed inside a Dialog
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val focusRequester = remember { FocusRequester() }
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

    val emptyLabelErrorToggle = remember { ToggleController((editedLabel.trim() == "")) }

    LaunchedEffect(editedLabel.trim()) {
        if (editedLabel.trim().isEmpty()) {
            emptyLabelErrorToggle.turnOn()
        } else {
            emptyLabelErrorToggle.turnOff()
        }
    }

    Row (
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (emptyLabelErrorToggle.isActive) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(
                    x = 0,
                    y = with(LocalDensity.current) { (-32).dp.roundToPx() }  // convert dp to px
                ),
                properties = PopupProperties(
                    focusable = false, // Doesn't steal click focus from the text field
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                )
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Label cannot be empty",
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        BasicTextField(
            value = editedLabel,
            onValueChange = { newValue -> onValueChange(newValue) },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
            modifier = modifier
                .focusRequester(focusRequester)
                .heightIn(min = 48.dp) // Match the standard button height
                .wrapContentHeight(Alignment.CenterVertically),
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            { focusRequester.requestFocus() }
        ) {
            Icon (
                imageVector = Icons.Default.Edit,
                contentDescription = "Editable label",
            )
        }
    }
}


@Composable
fun ColorSelector(color: TrackerColor?, onColorChoose: (TrackerColor?) -> Unit, modifier: Modifier = Modifier) {
    val toggleController = remember { ToggleController(false) }

    Row (
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            "Color",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.width(16.dp))

        Box {
            Button(
                { toggleController.turnOn() },
                colors = if (color == null) {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Gray
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = color.toComposeColor()
                    )
                },
                modifier = Modifier
                    .size(30.dp)
            ) { }

            // TODO: center DropdownMenu
            DropdownMenu(
                expanded = toggleController.isActive,
                onDismissRequest = { toggleController.turnOff() },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TrackerColor.entries.forEach { color ->
                        // TODO: add TooltipBox
                        Button(
                            {
                                onColorChoose(color)
                                toggleController.turnOff()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = color.toComposeColor()
                            ),
                            modifier = Modifier
                                .size(30.dp)
                        ) { }
                    }
                }
            }
        } // Box

        Spacer(modifier = Modifier.weight(1f))

//        val checked = remember { ToggleController((color != TrackerColor.Unspecified)) }
        val isChecked = { (color != null) }

        Switch(
            checked = isChecked(),
            onCheckedChange = { if (it) onColorChoose(TrackerColor.Red) else onColorChoose(null) },
            thumbContent = {
                if (isChecked()) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Enabled",
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Disabled",
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
    } // Row
}

@Composable
fun TrackerSettingDialog(tracker: Tracker, trackerCardSettingToggle: ToggleController) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val context = androidx.compose.ui.platform.LocalContext.current
    val db = remember { AppDatabase.getDatabase(context.applicationContext) }
    val trackerDao = db.trackerDao()

//    val tracker = trackerWithEvents.tracker
//    val events = trackerWithEvents.events

    var editedLabel by remember { mutableStateOf(tracker.label) }
    var editedColor by remember { mutableStateOf(tracker.color) }

    // TODO: create a new tracker if label or color is different and warn user about it
    val onReplace = {
        scope.launch {
            // Hide old and create new tracker
            trackerDao.updateTracker(tracker.copy(hidden = true))
            trackerDao.insertTracker(
                Tracker(
                    label = editedLabel.trim(),
                    color = editedColor
                )
            )
        }
    }

    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    Dialog(onDismissRequest = { trackerCardSettingToggle.turnOff() }) {
        // This Card acts as the "Menu" container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
//                Text(
//                    text = "Tracker Settings",
//                    style = MaterialTheme.typography.headlineSmall
//                )

//                Spacer(modifier = Modifier.height(16.dp))

                // Example Setting: Toggle for GMT vs Local

                TrackerLabelTextField(
                    editedLabel = editedLabel,
                    onValueChange = { newValue ->
                        editedLabel = newValue
                    },
                    isKeyboardOpen = isKeyboardOpen,
                    modifier = Modifier
                        .width(IntrinsicSize.Min)
                        .align(Alignment.Start)
                        .weight(1f)
                        .fadingEdge()
                )

                Spacer(modifier = Modifier.height(16.dp))

                ColorSelector(
                    editedColor,
                    { newColor -> editedColor = newColor}
                )

                Spacer(modifier = Modifier.height(24.dp))

                // TODO: make buttons look better
                Row {
                    Button(
                        onClick = { trackerCardSettingToggle.turnOff() },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.error,
//                            contentColor = MaterialTheme.colorScheme.onError
//                        ),
//                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dismiss")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    FilledIconButton(
                        onClick = {
                            scope.launch {
                                // Hide the tracker
                                trackerDao.updateTracker(tracker.copy(hidden = true))
                            }
                        },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.error,
//                            contentColor = MaterialTheme.colorScheme.onError
//                        ),
//                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (editedLabel.trim() == tracker.label && editedColor == tracker.color ) {
                                trackerCardSettingToggle.turnOff()
                            } else {
                                onReplace()
                            }
                        },
                        enabled = (editedLabel.trim() != ""),
//                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Replace")
                    }
                }

//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    "Replace button creates a new tracker instance",
//                    fontSize = 12.sp
//                )
            }
        } // Card
    } // Dialog
}