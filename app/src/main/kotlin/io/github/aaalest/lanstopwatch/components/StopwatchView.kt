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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import io.github.aaalest.lanstopwatch.data.Stopwatch
import io.github.aaalest.lanstopwatch.utils.VisibilityController

@Composable
fun LiveTimerText(startTime: Long, label: String) {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(key1 = startTime) {
        while (true) {
            currentTime = System.currentTimeMillis()
            val elapsed = (currentTime - startTime) % 1000
            println("Elapsed: $elapsed ms")
            kotlinx.coroutines.delay(1000 - elapsed)
        }
    }

    val elapsed = currentTime - startTime

    Text(text = "$label: ${elapsed / 1000}s ${elapsed % 1000}ms")
}

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
