package io.github.aaalest.lanstopwatch

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier

import io.github.aaalest.lanstopwatch.core.ui.theme.LanStopwatchTheme
//import io.github.aaalest.lanstopwatch.components.StopwatchView
import io.github.aaalest.lanstopwatch.tracker.data.Tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import io.github.aaalest.lanstopwatch.tracker.presentation.components.TrackerCard
import io.github.aaalest.lanstopwatch.tracker.data.AppDatabase
import io.github.aaalest.lanstopwatch.tracker.presentation.components.ActivityChart
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
                val trackersWithEvents by dao.getAllVisibleTrackers().collectAsState(initial = emptyList())
                val scope = rememberCoroutineScope()

                val configuration = LocalConfiguration.current
                val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val movableContentModifier = Modifier.fillMaxSize()
                    val movableContent = remember(trackersWithEvents) {
                        movableContentOf {
                            ActivityChart()

                            Box(
                                modifier = Modifier.fillMaxSize(),
                            ) {
                                val addButtonPadding = 24.dp
                                val addButtonSize = 96.dp
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    contentPadding = PaddingValues(bottom = addButtonPadding * 2 + addButtonSize),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .align(Alignment.TopCenter)
                                        .padding(12.dp, 0.dp)
                                ) {
                                    items(trackersWithEvents) { trackerWithEvents ->
                                        TrackerCard(trackerWithEvents, "Phone_01")
                                    }
//                                if (trackersWithEvents.isNotEmpty()) {
//                                    trackersWithEvents.forEach { trackerWithEvents ->
//                                        TrackerCard(trackerWithEvents, "Phone_01")
//                                    }
//                                }
                                }

                                FilledIconButton(
                                    onClick = {
                                        scope.launch {
                                            dao.insertTracker(
                                                Tracker(label = "New Tracker")
                                            )
                                        }
                                    },
//                                        colors = IconButtonDefaults.filledIconButtonColors(
//                                            containerColor = MaterialTheme.colorScheme.primary,
//                                            contentColor = MaterialTheme.colorScheme.onPrimary
//                                        ),
                                    modifier = Modifier
                                        .padding(bottom = addButtonPadding)
                                        .size(addButtonSize)
                                        .align(Alignment.BottomCenter)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add new tracker"
                                    )
                                }
                            }
                        }
                    } // content

                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        if (isLandscape) {
                            Row(modifier = movableContentModifier) {
                                movableContent()
                            }
                        } else {
                            Column(modifier = movableContentModifier) {
                                movableContent()
                            }
                        }
                    }

                    // TODO show a SnackbarHost with undo button when a recent tracker was removed
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
