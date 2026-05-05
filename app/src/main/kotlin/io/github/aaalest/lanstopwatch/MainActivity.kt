package io.github.aaalest.lanstopwatch

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier

import io.github.aaalest.lanstopwatch.core.ui.theme.LanStopwatchTheme
//import io.github.aaalest.lanstopwatch.components.StopwatchView
import io.github.aaalest.lanstopwatch.tracker.data.Tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import io.github.aaalest.lanstopwatch.tracker.presentation.components.TrackerCard
import io.github.aaalest.lanstopwatch.tracker.data.AppDatabase
import io.github.aaalest.lanstopwatch.tracker.presentation.components.ActivityCart
//import io.github.aaalest.lanstopwatch.data.sampleCards
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(applicationContext)
        val dao = db.trackerDao()

        enableEdgeToEdge()
        setContent {
            LanStopwatchTheme {
                val trackersWithEvents by dao.getAllTrackers().collectAsState(initial = emptyList())
                val scope = rememberCoroutineScope()

                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    val stopwatchEditActionController = VisibilityController()

//                    Box(Modifier
//                        .fillMaxWidth()
//                        .fillMaxHeight()
//                    ) {

                    val modifier = Modifier.padding(innerPadding).fillMaxSize()
                    val content = @Composable {
                        ActivityCart()

                        if (trackersWithEvents.isNotEmpty()) {
                            trackersWithEvents.forEach { trackerWithEvents ->
//                                StopwatchView(
//                                    card = stopwatch,
//                                    isFlipped = flashCardVisibilityController
//                                )
//                                    Text("Add New Stopwatch ${System.currentTimeMillis() - stopwatch.start}")
                                TrackerCard(trackerWithEvents, "Phone_01")
                            }
//                            FlashCardView(
//                                cards.first(),
//                                isFlipped = flashCardVisibilityController
//                            )
                        } else {
                            Button(
                                onClick = {
                                    scope.launch {
                                        dao.insertTracker(
                                            Tracker(label = "New Tracker")
                                        )
                                    }
                                }
                            ) {
                                Text("Add New Tracker")
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
                    } // content

                    if (isLandscape) {
                        Row(modifier = modifier, content = { content() })
                    } else {
                        Column(modifier = modifier, content = { content() })
                    }

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
