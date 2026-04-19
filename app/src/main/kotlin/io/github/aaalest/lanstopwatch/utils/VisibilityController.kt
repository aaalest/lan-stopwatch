package io.github.aaalest.lanstopwatch.utils

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf


interface VisibilityState {
    val isVisible: Boolean
    fun show()
    fun hide()
    fun toggle()
}

class VisibilityController : VisibilityState {
    private val _isVisible = mutableStateOf(false)
    override val isVisible get() = _isVisible.value
    override fun show() {
        _isVisible.value = true
    }

    override fun hide() {
        _isVisible.value = false
    }

    override fun toggle() {
        _isVisible.value = !_isVisible.value
    }
}

class PersistentVisibilityController(
    private val state: MutableState<Boolean>
) : VisibilityState {
    override val isVisible get() = state.value
    override fun show() {
        state.value = true
    }

    override fun hide() {
        state.value = false
    }

    override fun toggle() {
        state.value = !state.value
    }
}