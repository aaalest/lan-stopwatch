package io.github.aaalest.lanstopwatch.tracker.presentation

import androidx.compose.ui.graphics.Color

import io.github.aaalest.lanstopwatch.tracker.domain.TrackerColor

// TODO add fun TrackerColor.toMaterialColor
fun TrackerColor.toComposeColor(): Color = when (this) {
    TrackerColor.Red -> Color.Red
    TrackerColor.Green -> Color.Green
    TrackerColor.Blue -> Color.Blue
    TrackerColor.Yellow -> Color.Yellow
    TrackerColor.Cyan -> Color.Cyan
    TrackerColor.Magenta -> Color.Magenta
}