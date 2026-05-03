package io.github.aaalest.lanstopwatch.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


interface ToggleState {
    val isActive: Boolean
    fun turnOn()
    fun turnOff()
    fun toggle()
}

class ToggleController(initial: Boolean) : ToggleState {
    private val _isActive = mutableStateOf(initial)
    override val isActive by _isActive

    override fun turnOn() {
        _isActive.value = true
    }

    override fun turnOff() {
        _isActive.value = false
    }

    companion object {
        val Saver = Saver<ToggleController, Boolean>(
            save = { it.isActive },
            restore = { ToggleController(it) }
        )
    }

    override fun toggle() {
        _isActive.value = !_isActive.value
    }
}
