package cn.changjiahong.banker.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cn.changjiahong.banker.app.about.settings.template.TempSettingUiEvent

data class SimpleMenu(val title: String, val onClick: () -> Unit)

@Composable
fun RightClickMenu(menus: List<SimpleMenu>, content: @Composable BoxScope.() -> Unit) {
    RightClickMenu(menu={close->
        menus.forEach { menu ->
            DropdownMenuItem(text = {
                Text(menu.title)
            }, onClick = {
                menu.onClick()
                close()
            })
        }
    },content)
}


@Composable
fun RightClickMenu(
    menu: @Composable ColumnScope.(close: () -> Unit) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    Box(
        modifier = Modifier
            .wrapContentSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val mouseEvent = event.buttons

                        if (mouseEvent.isSecondaryPressed) {
                            val position = event.changes.first().position
                            offset = DpOffset(position.x.toDp(), position.y.toDp())
                            expanded = true
                        }
                    }
                }
            }) {


        Box {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = offset
            ) {
                menu {
                    expanded = false
                }
            }

        }

        content()
    }

}