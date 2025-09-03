package cn.changjiahong.banker.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class VisibleState {
    private val _visible = MutableStateFlow(false)
    val visible = _visible.asStateFlow()

    fun show() {
        _visible.value = true
    }

    fun dismiss() {
        _visible.value = false
    }
}

@Composable
fun rememberVisibleState(): VisibleState {
    return remember { VisibleState() }
}

@Composable
fun Visible(visibleState: VisibleState, content: @Composable () -> Unit) {
    val visible by visibleState.visible.collectAsState()
    if (visible) {
        content()
    }
}