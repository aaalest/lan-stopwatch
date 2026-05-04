package io.github.aaalest.lanstopwatch.tracker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.aaalest.lanstopwatch.core.utils.ToggleController


@Composable
fun BoxScope.FloatingEditActionButtons(
    visibilityController: ToggleController,
    onUndo: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    visibilityController.turnOn()
    AnimatedVisibility(
        visible = visibilityController.isActive,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
            .align(Alignment.BottomEnd) // Pinned to bottom right
            .padding(16.dp)             // Spacing from the card edge
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // UNDO - Small FAB style
            SmallFloatingActionButton(
                onClick = { onUndo() },
//                    editedLabel = tracker.label
//                    focusManager.clearFocus()
//                },
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Undo, "Undo")
            }

            // CONFIRM - Extended FAB style
            ExtendedFloatingActionButton(
                onClick = { onConfirm() },
//                    scope.launch {
//                        dao.upsertStopwatch(stopwatch.copy(label = editedLabel))
//                    }
//                    focusManager.clearFocus()
//                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            ) {
                Text("Confirm")
            }
        }
    }
}
