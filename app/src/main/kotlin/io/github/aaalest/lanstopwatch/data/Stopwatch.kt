package io.github.aaalest.lanstopwatch.data

import androidx.compose.ui.input.key.type
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

enum class EventType {
    PAUSE,
    RESUME,
}

data class TimeEvent(
    val eventType: EventType,
    val timestamp: Long,
    val deviceId: String
)

@Entity(tableName = "stopwatches")
data class Stopwatch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var label: String,
    var events: List<TimeEvent> = emptyList()
)

//val sampleCards = listOf(
//    Flashcard(word = "Ephemeral", definition = "Lasting for a very short time."),
//    Flashcard(word = "Mellifluous", definition = "Sweet and smooth sounding; pleasing to the ear."),
//    Flashcard(word = "Ubiquitous", definition = "Present, appearing, or found everywhere.")
//)
