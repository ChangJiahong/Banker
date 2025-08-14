package cn.changjiahong.banker.app.about.settings.business.tmp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.composable.BooleanFieldDropdown
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.composable.rememberDropdownScope
import cn.changjiahong.banker.platform.HorizontalScrollbar
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

class FieldConfigScreen(val business: Business, val template: DocTemplate) : Screen {
    @Composable
    override fun Content() {
        ScaffoldWithTopBar("字段配置") { pd ->
            FieldConfigView(Modifier.padding(pd))
        }
    }
}

@Composable
fun FieldConfigScreen.FieldConfigView(modifier: Modifier) {
    val fieldConfigScreenModel =
        koinScreenModel<FieldConfigScreenModel> { parametersOf(business, template) }
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

    Column(modifier.padding { paddingHorizontal(10.dp) }) {

        val templateOptions by fieldConfigScreenModel.templateOptions.collectAsState()
        val businessOptions by fieldConfigScreenModel.businessOptions.collectAsState()
        val userOptions by fieldConfigScreenModel.userOptions.collectAsState()

        val btFieldConfigs by fieldConfigScreenModel.btFieldConfigs.collectAsState()
        val btFieldConfigsError by fieldConfigScreenModel.btFieldConfigsError.collectAsState()

        val tuFieldConfigs by fieldConfigScreenModel.tuFieldConfigs.collectAsState()
        val tuFieldConfigsError by fieldConfigScreenModel.tuFieldConfigsError.collectAsState()

        val tempFieldOptions =
            rememberDropdownScope(templateOptions)
        val businessFieldOptions =
            rememberDropdownScope(businessOptions)
        val userFieldOptions =
            rememberDropdownScope(userOptions)


        val groups = remember { mutableStateListOf("") }


        Row(Modifier.padding {
            paddingVertical(5.dp)
        }, verticalAlignment = Alignment.CenterVertically) {
            Text("基本信息", fontSize = 24.sp)
            IconButton({
                FieldConfigScreenUiEvent.AddUFieldConfig.sendTo(fieldConfigScreenModel)
            }) {
                Icon(painter = painterResource(Res.drawable.home), contentDescription = "")
            }
        }
        HorizontalDivider()
        Card(modifier = Modifier.padding {
            paddingVertical(5.dp)
        }) {
            Column(
                modifier = Modifier.padding(5.dp).heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(Modifier.fillMaxWidth()) {
                    tuFieldConfigs.forEachIndexed { index, field ->
                        Row {

                            var item by remember(field) { mutableStateOf(field) }
                            var itemError by remember(tuFieldConfigsError[index]) {
                                mutableStateOf(
                                    tuFieldConfigsError[index]
                                )
                            }

                            val englishRegex = Regex("^$|^(?=.*[a-zA-Z])[a-zA-Z0-9]*$")

                            TextFieldDropdown(
                                tempFieldOptions,
                                item.tempFieldId,
                                onValueSelected = {
                                    item = item.copy(tempFieldId = it)
                                    FieldConfigScreenUiEvent.UpdateUFiled(
                                        index,
                                        item
                                    ).sendTo(fieldConfigScreenModel)
                                },
                                "表单名",
                                enableEdit = false,
                                errorText = itemError.tempFieldId,
                                modifier = Modifier.width(150.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )

                            TextFieldDropdown(
                                userFieldOptions,
                                item.userFieldId,
                                onValueSelected = {
                                    item = item.copy(userFieldId = it)
                                    FieldConfigScreenUiEvent.UpdateUFiled(
                                        index,
                                        item
                                    ).sendTo(fieldConfigScreenModel)
                                },
                                label = "字段名",
                                errorText = itemError.userFieldId,
                                modifier = Modifier.width(150.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                        }

                    }
                }
            }
        }

        Row(Modifier.padding {
            paddingVertical(5.dp)
        }, verticalAlignment = Alignment.CenterVertically) {
            Text("业务信息", fontSize = 24.sp)
            IconButton({
                FieldConfigScreenUiEvent.AddBFieldConfig.sendTo(fieldConfigScreenModel)
            }) {
                Icon(painter = painterResource(Res.drawable.home), contentDescription = "")
            }
        }
        HorizontalDivider()
        val scrollState = rememberScrollState()

        Card(modifier = Modifier.padding {
            paddingVertical(5.dp)
        }) {
            Column(
                modifier = Modifier.padding(5.dp).heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(Modifier.fillMaxWidth().horizontalScroll(scrollState)) {

                    btFieldConfigs.forEachIndexed { index, btField ->

                        Row {

                            var item by remember(btField) { mutableStateOf(btField) }
                            var itemError by remember(btFieldConfigsError[index]) {
                                mutableStateOf(
                                    btFieldConfigsError[index]
                                )
                            }

                            val englishRegex = Regex("^$|^(?=.*[a-zA-Z])[a-zA-Z0-9]*$")

                            TextFieldDropdown(
                                tempFieldOptions,
                                item.tempFieldId,
                                onValueSelected = {
                                    item = item.copy(tempFieldId = it)
                                    FieldConfigScreenUiEvent.UpdateBusinessFiled(
                                        index,
                                        item
                                    ).sendTo(fieldConfigScreenModel)
                                },
                                "表单名",
                                enableEdit = false,
                                errorText = itemError.tempFieldId,
                                modifier = Modifier.width(150.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
//                                TextFieldDropdown(
//                                    item.fieldType,
//                                    onValueChange = {
//                                        item = item.copy(fieldType = it)
//                                        FieldConfigScreenUiEvent.UpdateBusinessFiled(
//                                            index,
//                                            item
//                                        )
//                                            .sendTo(fieldConfigScreenModel)
//                                    },
//                                    listOf("TEXT", "IMAGE"),
//                                    enableEdit = false,
//                                    label = "字段类型",
//                                    modifier = Modifier.width(140.dp)
//                                        .padding { paddingHorizontal(2.dp) }
//                                )


                            BooleanFieldDropdown(
                                item.isFixed,
                                onValueChange = {
                                    item = item.copy(isFixed = it)
                                    FieldConfigScreenUiEvent.UpdateBusinessFiled(
                                        index,
                                        item
                                    ).sendTo(fieldConfigScreenModel)
                                },
                                "是否固定值",
                                modifier = Modifier.width(110.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )




                            if (item.isFixed) {
                                InputView(
                                    value = item.fixedValue ?: "",
                                    onValueChange = {
                                        item = item.copy(fixedValue = it)
                                        FieldConfigScreenUiEvent.UpdateBusinessFiled(
                                            index,
                                            item
                                        ).sendTo(fieldConfigScreenModel)
                                    },
                                    label = "固定值",
                                    errorText = itemError.fixedValue,
                                    modifier = Modifier.width(150.dp)
                                        .padding { paddingHorizontal(2.dp) }
                                )
                            } else {

                                TextFieldDropdown(
                                    businessFieldOptions,
                                    item.businessFieldId,
                                    onValueSelected = {
                                        item = item.copy(businessFieldId = it)
                                        FieldConfigScreenUiEvent.UpdateBusinessFiled(
                                            index,
                                            item
                                        ).sendTo(fieldConfigScreenModel)
                                    },
                                    label = "字段名",
                                    errorText = itemError.businessFieldId,
                                    modifier = Modifier.width(150.dp)
                                        .padding { paddingHorizontal(2.dp) }
                                )
                            }
                        }
                    }
                }
                HorizontalScrollbar(scrollState)
            }


        }

        Button({
            FieldConfigScreenUiEvent.SaveConfig.sendTo(fieldConfigScreenModel)
        }) {
            Text("保存")
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