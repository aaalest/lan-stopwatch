package io.github.aaalest.lanstopwatch.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp


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
fun StopwatchTimeDisplay(
    elapsedSeconds: Long,
    modifier: Modifier = Modifier
) {
    Text(
        text = formatTime(elapsedSeconds),
        modifier = modifier
    )
}