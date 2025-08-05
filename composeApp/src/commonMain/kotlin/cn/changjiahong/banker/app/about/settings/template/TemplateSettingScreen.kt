package cn.changjiahong.banker.app.about.settings.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_box
import banker.composeapp.generated.resources.arrow_back
import banker.composeapp.generated.resources.pdf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.ScaffoldWithTopBar
import org.jetbrains.compose.resources.painterResource

object TemplateSettingScreen : Screen {
    @Composable
    override fun Content() = TemplateSettingView()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSettingScreen.TemplateSettingView(tempSettingScreenModel: TemplateSettingScreenModel = koinScreenModel()) {

    ScaffoldWithTopBar("模版文件维护"){ paddingValues ->
        TempFileGridView(modifier = Modifier.padding(paddingValues), tempSettingScreenModel)
    }
}

@Composable
fun TempFileGridView(
    modifier: Modifier = Modifier,
    tempSettingScreenModel: TemplateSettingScreenModel
) {

    val tempFiles by tempSettingScreenModel.tempFiles.collectAsState()
    LazyVerticalGrid(
        GridCells.FixedSize(100.dp), contentPadding = PaddingValues(5.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(tempFiles) { index, item ->
            RightClickMenu(menu = {
                DropdownMenuItem(text = {
                    Text("预览")
                }, onClick = {
                    TempSettingUiEvent.GoPreTemplateScreen(item).sendTo(tempSettingScreenModel)
                    it()
                })
                DropdownMenuItem(text = {
                    Text("查看表单")
                }, onClick = {
                    TempSettingUiEvent.GoTempFiledSettingScreen(item).sendTo(tempSettingScreenModel)
                    it()
                })
            }) {
                FoldersButton(item.templateName, painterResource(Res.drawable.pdf)) {
                    TempSettingUiEvent.GoPreTemplateScreen(item).sendTo(tempSettingScreenModel)
                }
            }
        }

        item {
            FoldersButton(icon = painterResource(Res.drawable.add_box)) {

            }
        }
    }
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