package cn.changjiahong.banker.app.about.settings.business.tmp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_diamond
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.composable.BooleanFieldDropdown
import cn.changjiahong.banker.composable.HoverDeleteBox
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.composable.rememberDropdownScope
import cn.changjiahong.banker.platform.HorizontalScrollbar
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

class FieldConfigScreen(val business: Business, val template: Template) : Screen {
    @Composable
    override fun Content() {
        val fieldConfigScreenModel =
            koinScreenModel<FieldConfigScreenModel> { parametersOf(business, template) }

        ScaffoldWithTopBar(
            "字段配置", iconPainter = painterResource(Res.drawable.add_diamond),
            iconOnClick = {
                FieldConfigScreenUiEvent.AddFieldConfig.sendTo(fieldConfigScreenModel)
            }
        ) { pd ->
            FieldConfigView(Modifier.padding(pd), fieldConfigScreenModel)
        }
    }
}

@Composable
fun FieldConfigScreen.FieldConfigView(
    modifier: Modifier,
    fieldConfigScreenModel: FieldConfigScreenModel
) {
    val globalNavigator = GlobalNavigator.current

    fieldConfigScreenModel.handleEffect {
        when (it) {
            is FieldConfigScreenUiEffect.SaveSuccess -> {
                globalNavigator.pop()
                true
            }

            else -> false
        }
    }

    Column(modifier.padding { paddingHorizontal(30.dp) }) {

        val tplFieldOptions by fieldConfigScreenModel.tplFieldOptions.collectAsState()
        val fieldOptions by fieldConfigScreenModel.fieldOptions.collectAsState()

        val fieldConfigs by fieldConfigScreenModel.fieldConfigs.collectAsState()
        val fieldConfigsError by fieldConfigScreenModel.fieldConfigsError.collectAsState()

        val tempFieldOptions =
            rememberDropdownScope(tplFieldOptions)
        val fieldConfigOptions =
            rememberDropdownScope(fieldOptions)

        Button({
            FieldConfigScreenUiEvent.SaveConfig.sendTo(fieldConfigScreenModel)
        }) {
            Text("保存")
        }

        HorizontalDivider()
        val scrollState = rememberScrollState()

        Card(modifier = Modifier.padding {
            paddingVertical(5.dp)
        }) {
            Column(
                modifier = Modifier.padding(5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                var ind = remember { 1 }

                fieldConfigs.forEachIndexed { index, btField ->
                    if (btField.isDelete) {
                        return@forEachIndexed
                    }
                    Row(
                        Modifier.wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        var item by remember(btField) { mutableStateOf(btField) }
                        var itemError by remember(fieldConfigsError[index]) {
                            mutableStateOf(
                                fieldConfigsError[index]
                            )
                        }

                        val englishRegex = Regex("^$|^(?=.*[a-zA-Z])[a-zA-Z0-9]*$")

                        HoverDeleteBox((ind++).toString(), Modifier.padding {
                            paddingHorizontal(3.dp)
                            paddingTop(5.dp)
                        }) {
                            ConfigUiEvent.Delete(index).sendTo(fieldConfigScreenModel)
                        }

                        TextFieldDropdown(
                            tempFieldOptions,
                            item.tFieldId,
                            onValueSelected = {
                                item = item.copy(tFieldId = it)
                                FieldConfigScreenUiEvent.UpdateFiledConfig(
                                    index,
                                    item
                                ).sendTo(fieldConfigScreenModel)
                            },
                            "表单名",
                            enableEdit = false,
                            enableCancel = true,
                            errorText = itemError.tempFieldId,
                            modifier = Modifier.width(200.dp)
                                .padding { paddingHorizontal(2.dp) }
                        )


                        BooleanFieldDropdown(
                            item.isFixed,
                            onValueChange = {
                                item = item.copy(isFixed = it)
                                FieldConfigScreenUiEvent.UpdateFiledConfig(
                                    index,
                                    item
                                ).sendTo(fieldConfigScreenModel)
                            },
                            "是否固定",
                            modifier = Modifier.width(90.dp)
                                .padding { paddingHorizontal(2.dp) }
                        )




                        if (item.isFixed) {
                            InputView(
                                value = item.fixedValue ?: "",
                                onValueChange = {
                                    item = item.copy(fixedValue = it)
                                    FieldConfigScreenUiEvent.UpdateFiledConfig(
                                        index,
                                        item
                                    ).sendTo(fieldConfigScreenModel)
                                },
                                label = "固定值",
                                errorText = itemError.fixedValue,
                                modifier = Modifier.width(200.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                        } else {

                            TextFieldDropdown(
                                fieldConfigOptions,
                                item.fieldId,
                                onValueSelected = {
                                    item = item.copy(fieldId = it)
                                    FieldConfigScreenUiEvent.UpdateFiledConfig(
                                        index,
                                        item
                                    ).sendTo(fieldConfigScreenModel)
                                },
                                label = "字段名",
                                errorText = itemError.businessFieldId,
                                modifier = Modifier.width(200.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                        }
                    }
                }
            }


        }


    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TextFieldDropdown1(
        allOptions: List<String>,
        selectedOptions: List<String> = emptyList(),
        enabled: Boolean = true,
        label: String = "",
        modifier: Modifier = Modifier,
        keyboardType: KeyboardType = KeyboardType.Text,
        inputText: String,
        onValueChange: (String) -> Unit,
    ) {
        var expanded by remember { mutableStateOf(false) }
//    var inputText by rememberSaveable() { mutableStateOf("") }


        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = modifier
        ) {

            OutlinedTextField(
                value = inputText,
                onValueChange = onValueChange,
                enabled = enabled,
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allOptions.filterNot { it in selectedOptions }
                    .filter { it.contains(inputText, ignoreCase = true) }
                    .forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onValueChange(option)
                                expanded = false
                            }
                        )
                    }
            }
        }
    }

}