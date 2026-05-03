package io.github.aaalest.lanstopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import io.github.aaalest.lanstopwatch.ui.theme.LanStopwatchTheme
//import io.github.aaalest.lanstopwatch.components.StopwatchView
import io.github.aaalest.lanstopwatch.data.Stopwatch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import io.github.aaalest.lanstopwatch.components.StopwatchCard
import io.github.aaalest.lanstopwatch.data.AppDatabase
import io.github.aaalest.lanstopwatch.data.EventType
import io.github.aaalest.lanstopwatch.data.TimeEvent
//import io.github.aaalest.lanstopwatch.data.sampleCards
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.stopwatchDao()

        enableEdgeToEdge()
        setContent {
            LanStopwatchTheme {
                val stopwatches by dao.getAllStopwatches().collectAsState(initial = emptyList())
                val scope = rememberCoroutineScope()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    val stopwatchEditActionController = VisibilityController()

//                    Box(Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
//                    ) {
                    Column(Modifier.padding(innerPadding)) {
                        if (stopwatches.isNotEmpty()) {
                            stopwatches.forEach { stopwatch ->
//                                StopwatchView(
//                                    card = stopwatch,
//                                    isFlipped = flashCardVisibilityController
//                                )
//                                    Text("Add New Stopwatch ${System.currentTimeMillis() - stopwatch.start}")
                                StopwatchCard(stopwatch, "Phone_01")
                            }
//                            FlashCardView(
//                                cards.first(),
//                                isFlipped = flashCardVisibilityController
//                            )
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        dao.upsertStopwatch(
                                            Stopwatch(label = "New Stopwatch")
                                        )
                                    }
                                }
                            ) {
                                Text("Add New Stopwatch")
                            }
//                                Text(
//                                    "No cards. Tap to add sample data.",
//                                    modifier = Modifier.padding(16.dp).clickable {
//                                        scope.launch {
//                                            sampleCards.forEach { dao.upsertFlashcard(it) }
//                                        }
//                                    }
//                                )
                        }
                    } // Column

//                        FloatingEditActionButtons(
//                            stopwatchEditActionController,
//                            onUndo = {},
//                            onConfirm = {},
//                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))  // picks the highest value
//                        )
//                    } // Box
                }
            }
        }
    }
}
