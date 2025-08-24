package cn.changjiahong.banker.composable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

class TipState() : DialogState() {
    private var _tip: String = ""
    private var _title: String = ""
    val tip get() = _tip
    val title get() = _title

    fun show(tip: String, title: String = "提示") {
        this._tip = tip
        this._title = title
        show()
    }
}

@Composable
fun rememberTipState(): TipState {
    return remember { TipState() }
}

@Composable
fun TipDialog(
    dialogState: TipState = rememberTipState(),
    onDismissRequest: () -> Unit = { dialogState.dismiss() }
) {
    val visible by dialogState.visible.collectAsState()
    if (!visible) {
        return
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { Button(onClick = { onDismissRequest() }) { Text("确定") } },
        title = { Text(dialogState.title) },
        text = { Text(dialogState.tip) })
}