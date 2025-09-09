package cn.changjiahong.banker.app.about.settings.global

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_diamond
import banker.composeapp.generated.resources.home
import banker.composeapp.generated.resources.remove
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.app.about.settings.business.tmp.BFieldConfigScreenUiEvent
import cn.changjiahong.banker.composable.BooleanFieldDropdown
import cn.changjiahong.banker.composable.HoverDeleteBox
import cn.changjiahong.banker.composable.SwitchButton
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.model.fieldTypes
import cn.changjiahong.banker.model.isTableType
import cn.changjiahong.banker.platform.HorizontalScrollbar
import cn.changjiahong.banker.utils.padding
import com.alorma.compose.settings.ui.SettingsRadioButton
import com.alorma.compose.settings.ui.SettingsSwitch
import org.jetbrains.compose.resources.painterResource

class GlobalFieldSettingScreen : Screen {

    @Composable
    override fun Content() {
        val fieldConfigScreenModel = koinScreenModel<GlobalFieldSettingScreenModel>()

        ScaffoldWithTopBar(
            "全局字段信息",
            iconPainter = painterResource(Res.drawable.add_diamond),
            iconOnClick = {
                ConfigUiEvent.Add.sendTo(fieldConfigScreenModel)
            }) { pd ->
            ExtendFieldSettingView(Modifier.padding(pd), fieldConfigScreenModel)
        }
    }
}

@Composable
private fun GlobalFieldSettingScreen.ExtendFieldSettingView(
    modifier: Modifier,
    fieldConfigScreenModel: GlobalFieldSettingScreenModel
) {

    val global = GlobalNavigator.current
    fieldConfigScreenModel.handleEffect {
        when {
            it is ConfigUiEffect.SaveSuccess -> {
                global.pop()
                true
            }

            else -> false
        }
    }
    Column(modifier.padding { paddingHorizontal(30.dp) }) {
        val metaConfigs by fieldConfigScreenModel.metaConfigs.collectAsState()
        val errors by fieldConfigScreenModel.errors.collectAsState()

        Button({
            ConfigUiEvent.Save.sendTo(fieldConfigScreenModel)
        }) {
            Text("保存")
        }
        HorizontalDivider()
        val scrollState = rememberScrollState()

        Card(modifier = Modifier.padding {
            paddingVertical(5.dp)
        }) {
            val verticalScrollState = rememberScrollState()
            LaunchedEffect(metaConfigs.size) {
                // 滚动到底部
                if (metaConfigs.isNotEmpty()) {
                    verticalScrollState.animateScrollTo(verticalScrollState.maxValue)
                }
            }
            Column(
                modifier = Modifier.padding(5.dp)
                    .verticalScroll(verticalScrollState)
            ) {
                var ind = remember { 1 }
                Column(Modifier.wrapContentWidth()) {
                    metaConfigs.forEachIndexed { index, uiMeta ->
                        if (uiMeta.isDelete) {
                            return@forEachIndexed
                        }
                        Row(
                            Modifier.wrapContentWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            val bfe = errors[index]

                            var item by remember(uiMeta) { mutableStateOf(uiMeta) }
                            var error by remember(bfe) { mutableStateOf(bfe) }

                            HoverDeleteBox((ind++).toString(), Modifier.padding {
                                paddingHorizontal(3.dp)
                                paddingTop(5.dp)
                            }) {
                                ConfigUiEvent.Delete(index).sendTo(fieldConfigScreenModel)
                            }

                            InputView(
                                value = item.label,
                                onValueChange = {
                                    item = item.copy(label = it)
                                    GlobalConfigUiEvent.Update(index, item)
                                        .sendTo(fieldConfigScreenModel)
                                },
                                label = "字段名",
                                errorText = error.label,
                                readOnly = false,
                                modifier = Modifier.width(150.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                            TextFieldDropdown(
                                fieldTypes(),
                                item.metaType,
                                onValueChange = {
                                    item = item.copy(metaType = it)
                                    GlobalConfigUiEvent.Update(index, item)
                                        .sendTo(fieldConfigScreenModel)
                                },
                                enableEdit = false,
                                label = "字段类型",
                                modifier = Modifier.width(160.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )

                            SwitchButton(
                                Modifier.padding { paddingHorizontal(2.dp) },
                                checked = item.forced, label = "必输项", onCheckedChange = {
                                    item = item.copy(forced = it)
                                    GlobalConfigUiEvent.Update(index, item)
                                        .sendTo(fieldConfigScreenModel)
                                })

                            SwitchButton(
                                Modifier.padding { paddingHorizontal(2.dp) },
                                checked = item.isGlobal,
                                label = "全局共享",
                                onCheckedChange = {
                                    item = item.copy(isGlobal = it)
                                    GlobalConfigUiEvent.Update(index, item)
                                        .sendTo(fieldConfigScreenModel)
                                })


                            InputView(
                                value = item.width.toString(),
                                onValueChange = { newValue ->
                                    val digitsOnly = newValue.filter { it.isDigit() }
                                    // 更新状态
                                    val newWidth = digitsOnly.toIntOrNull() ?: 0
                                    item = item.copy(width = newWidth)

                                    GlobalConfigUiEvent.Update(index, item)
                                        .sendTo(fieldConfigScreenModel)

                                },
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                label = "长度",
                                errorText = error.width,
                                modifier = Modifier.width(90.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                            InputView(
                                value = item.validation,
                                onValueChange = {
                                    item = item.copy(validation = it)
                                    GlobalConfigUiEvent.Update(index, item)
                                        .sendTo(fieldConfigScreenModel)

                                },
                                label = "校验规则",
                                errorText = error.validation,
                                modifier = Modifier.width(160.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )


                            if (item.metaType.isTableType()) {
                                InputView(
                                    value = item.options,
                                    onValueChange = {
                                        item = item.copy(options = it)
                                        GlobalConfigUiEvent.Update(index, item)
                                            .sendTo(fieldConfigScreenModel)
                                    },
                                    label = "选项列表",
                                    errorText = error.options,
                                    readOnly = false,
                                    modifier = Modifier.width(260.dp)
                                        .padding { paddingHorizontal(2.dp) }
                                )
                            }
                        }
                    }

                }
            }
        }

    }
}