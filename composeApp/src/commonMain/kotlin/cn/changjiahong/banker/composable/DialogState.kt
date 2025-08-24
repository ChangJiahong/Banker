package cn.changjiahong.banker.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class DialogState {
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
fun rememberDialogState(): DialogState {
    return remember { DialogState() }
}