package cn.changjiahong.banker.app.about.settings.template

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.business.tmp.BFieldConfigScreenUiEvent
import cn.changjiahong.banker.app.about.settings.business.tmp.FieldConfigScreenUiEvent
import cn.changjiahong.banker.composable.BooleanFieldDropdown
import cn.changjiahong.banker.composable.Option
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.composable.TextFieldDropdownScope
import cn.changjiahong.banker.composable.rememberDropdownScope
import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.TempField
import cn.changjiahong.banker.model.TempFieldError
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

class TempFieldSettingScreen(val template: DocTemplate) : Screen {
    @Composable
    override fun Content() {
        val globalNavigator = GlobalNavigator.current
        val tempFieldSettingScreenModel =
            koinScreenModel<TempFieldSettingScreenModel> { parametersOf(template) }
        tempFieldSettingScreenModel.handleEffect {
            when (it) {
                is TFSUiEffect.SaveSuccess -> {
                    globalNavigator.pop()
                    true
                }

                else -> false
            }
        }
        ScaffoldWithTopBar("表单设置", iconOnClick = {
            TFSUiEvent.AddNewFieldConfig.sendTo(tempFieldSettingScreenModel)
        }, iconPainter = painterResource(Res.drawable.home)) { pd ->
            TempFieldView(Modifier.padding(pd), tempFieldSettingScreenModel)
        }
    }
}

@Composable
fun TempFieldSettingScreen.TempFieldView(
    modifier: Modifier,
    tempFieldSettingScreenModel: TempFieldSettingScreenModel
) {


    Column(modifier) {

        val tempFieldConfigs by tempFieldSettingScreenModel.tempFieldConfigs.collectAsState()
        val tempFieldConfigsError by tempFieldSettingScreenModel.tempFieldConfigsError.collectAsState()

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
                    modifier = Modifier.padding(5.dp).heightIn(max = 0.8f * maxHeight)
                        .verticalScroll(verticalScrollState)
                ) {
                    Column(Modifier.fillMaxWidth().horizontalScroll(scrollState)) {

                        val tempFormFields by tempFieldSettingScreenModel.tempFormFields.collectAsState()

                        val scope =
                            rememberDropdownScope(tempFormFields.map { Option(it.name, it) })

                        tempFieldConfigs.forEachIndexed { index, field ->

                            FieldConfigItem(scope, field, tempFieldConfigsError[index]) {
                                TFSUiEvent.UpdateTempFieldConfig(index, it)
                                    .sendTo(tempFieldSettingScreenModel)
                            }
                        }
                    }
                }
            }
        }

        Button({
            TFSUiEvent.SaveConfig.sendTo(tempFieldSettingScreenModel)
        }) {
            Text("保存")
        }
    }
//HorizontalScrollbar(scrollState)

}


@Composable
private fun FieldConfigItem(
    scope: TextFieldDropdownScope<FormField>,
    tempField: TempField,
    tempFieldError: TempFieldError,
    updateTempField: (TempField) -> Unit
) {
    var item by remember(tempField) { mutableStateOf(tempField) }
    var error by remember(tempFieldError) { mutableStateOf(tempFieldError) }

    var field by remember() { mutableStateOf<FormField?>(scope.options.find { it.label == item.fieldName }?.value) }

    Row {

        TextFieldDropdown(
            scope,
            field,
            {
                field = it
                item = item.copy(fieldName = it?.name)
                if (item.fieldType == null) {
                    item = item.copy(fieldType = it?.type)
                }
                if (item.alias == null) {
                    item = item.copy(alias = it?.name)
                }
                updateTempField(item)
            }, "字段名",
            enableEdit = false,
            modifier = Modifier.width(200.dp)
                .padding { paddingHorizontal(2.dp) }
        )


        TextFieldDropdown(
            value = item.fieldType ?: "",
            onValueChange = {
                item = item.copy(fieldType = it)
                updateTempField(item)
            },
            options = listOf("TEXT","CHECK", "IMAGE"),
            enableEdit = false,
            label = "字段类型",
            errorText = error.fieldType,
            modifier = Modifier.width(140.dp)
                .padding { paddingHorizontal(2.dp) }
        )

        InputView(
            value = item.alias ?: "",
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