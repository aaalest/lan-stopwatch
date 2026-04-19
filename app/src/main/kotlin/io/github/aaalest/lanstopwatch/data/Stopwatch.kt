package io.github.aaalest.lanstopwatch.data

import androidx.compose.ui.input.key.type
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTimeEventList(value: List<TimeEvent>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toTimeEventList(value: String): List<TimeEvent> {
        val listType = object : TypeToken<List<TimeEvent>>() {}.type
        return gson.fromJson(value, listType)
    }
}

enum class EventType {
    START,
    PAUSE,
    RESUME,
}

data class TimeEvent(
    val deviceId: String,
    val eventType: EventType,
    val timestamp: Long
)

@Entity(tableName = "stopwatches")
data class Stopwatch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val label: String,
    val start: Long,

)

//val sampleCards = listOf(
//    Flashcard(word = "Ephemeral", definition = "Lasting for a very short time."),
//    Flashcard(word = "Mellifluous", definition = "Sweet and smooth sounding; pleasing to the ear."),
//    Flashcard(word = "Ubiquitous", definition = "Present, appearing, or found everywhere.")
//)
