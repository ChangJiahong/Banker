package cn.changjiahong.banker.app.about.settings.template

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_diamond
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.composable.HoverDeleteBox
import cn.changjiahong.banker.composable.Option
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.composable.TextFieldDropdownScope
import cn.changjiahong.banker.composable.rememberDropdownScope
import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.TplFieldConfigError
import cn.changjiahong.banker.model.TplFieldConfig
import cn.changjiahong.banker.model.fieldTypes
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

class TempFieldSettingScreen(val template: Template) : Screen {
    @Composable
    override fun Content() {
        val globalNavigator = GlobalNavigator.current
        val tempFieldConfigScreenModel =
            koinScreenModel<TempFieldConfigScreenModel> { parametersOf(template) }
        tempFieldConfigScreenModel.handleEffect {
            when (it) {
                is TFSUiEffect.SaveSuccess -> {
                    globalNavigator.pop()
                    true
                }

                else -> false
            }
        }
        ScaffoldWithTopBar("表单设置", iconOnClick = {
            TFSUiEvent.AddNewFieldConfig.sendTo(tempFieldConfigScreenModel)
        }, iconPainter = painterResource(Res.drawable.add_diamond)) { pd ->
            TempFieldView(Modifier.padding(pd), tempFieldConfigScreenModel)
        }
    }
}

@Composable
fun TempFieldSettingScreen.TempFieldView(
    modifier: Modifier,
    tempFieldConfigScreenModel: TempFieldConfigScreenModel
) {


    Column(modifier.padding {
        paddingHorizontal(30.dp)
    }) {

        val tempFieldConfigs by tempFieldConfigScreenModel.tempFieldConfigs.collectAsState()
        val tempFieldConfigsError by tempFieldConfigScreenModel.tempFieldConfigsError.collectAsState()
        Button({
            TFSUiEvent.SaveConfig.sendTo(tempFieldConfigScreenModel)
        }) {
            Text("保存")
        }
        HorizontalDivider()
        val scrollState = rememberScrollState()

        Card(modifier = Modifier.padding {
            paddingVertical(5.dp)
        }) {
            BoxWithConstraints {
                val verticalScrollState = rememberScrollState()
                LaunchedEffect(tempFieldConfigs.size) {
                    // 滚动到底部
                    if (tempFieldConfigs.isNotEmpty()) {
                        verticalScrollState.animateScrollTo(verticalScrollState.maxValue)
                    }
                }
                Column(
                    modifier = Modifier.padding(5.dp)
                        .verticalScroll(verticalScrollState)
                ) {


                    val tempFormFields by tempFieldConfigScreenModel.tempFormFields.collectAsState()

                    val scope =
                        rememberDropdownScope(tempFormFields.map { Option(it.name, it) })
                    var ind = remember { 1 }

                    tempFieldConfigs.forEachIndexed { index, field ->
                        if (field.isDelete) {
                            return@forEachIndexed
                        }
                        FieldConfigItem(
                            ind++,
                            scope, field, tempFieldConfigsError[index],
                            deleteField = {
                                ConfigUiEvent.Delete(index).sendTo(tempFieldConfigScreenModel)
                            }) {
                            TFSUiEvent.UpdateTempFieldConfig(index, it)
                                .sendTo(tempFieldConfigScreenModel)
                        }
                    }

                }
            }
        }


    }
//HorizontalScrollbar(scrollState)

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldConfigItem(
    ind: Int,
    scope: TextFieldDropdownScope<FormField>,
    tempField: TplFieldConfig,
    tempFieldError: TplFieldConfigError,
    deleteField: () -> Unit,
    updateTempField: (TplFieldConfig) -> Unit,
) {
    var item by remember(tempField) { mutableStateOf(tempField) }
    var error by remember(tempFieldError) { mutableStateOf(tempFieldError) }

    var field by remember(tempField) { mutableStateOf(scope.options.find { it.label == item.fieldName }?.value) }

    Row(Modifier.wrapContentWidth(), verticalAlignment = Alignment.CenterVertically) {

        HoverDeleteBox((ind).toString(), Modifier.padding {
            paddingHorizontal(3.dp)
            paddingTop(5.dp)
        }) {
            deleteField()
        }

        TextFieldDropdown(
            scope,
            field,
            {
                field = it
                item = item.copy(fieldName = it?.name ?: "")
                if (item.fieldType != it?.type) {
                    item = item.copy(fieldType = it?.type ?: "")
                }
                if (item.alias.isBlank()) {
                    item = item.copy(alias = it?.name ?: "")
                }
                updateTempField(item)
            }, "字段名",
            enableEdit = false,
            enableCancel = true,
            errorText = error.fieldName,
            modifier = Modifier.width(200.dp)
                .padding { paddingHorizontal(2.dp) }
        )


        TextFieldDropdown(
            value = item.fieldType,
            onValueChange = {
                item = item.copy(fieldType = it)
                updateTempField(item)
            },
            options = fieldTypes(),
            enableEdit = false,
            label = "字段类型",
            errorText = error.fieldType,
            modifier = Modifier.width(140.dp)
                .padding { paddingHorizontal(2.dp) }
        )

        InputView(
            value = item.alias,
            onValueChange = {
                item = item.copy(alias = it)
                updateTempField(item)
            },
            label = "别名",
            errorText = error.alias,
            modifier = Modifier.width(200.dp)
                .padding { paddingHorizontal(2.dp) }
        )

    }
}