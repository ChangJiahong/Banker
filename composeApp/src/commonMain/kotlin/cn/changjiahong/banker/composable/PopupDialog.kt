package cn.changjiahong.banker.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.resources.painterResource


@Composable
fun PopupDialog(
    popupDialogState: DialogState,
    title: String = "",
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {

    val visible by popupDialogState.visible.collectAsState()
    if (!visible) {
        return
    }

    Popup(
        alignment = Alignment.Center,
        onDismissRequest = { popupDialogState.dismiss() },
        properties = PopupProperties(focusable = true)
    ) {

        Card(
            modifier = modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 左侧
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {

                    }
                    // 中间
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(title)
                    }
                    // 右侧
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {

                        IconButton({ popupDialogState.dismiss() }, modifier = Modifier) {
                            Icon(
                                painter = painterResource(Res.drawable.cancel),
                                contentDescription = ""
                            )
                        }
                    }

                }
                content()
            }
        }

    }
}