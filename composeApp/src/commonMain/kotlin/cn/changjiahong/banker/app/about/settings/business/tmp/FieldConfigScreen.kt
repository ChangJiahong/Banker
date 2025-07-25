package cn.changjiahong.banker.app.about.settings.business.tmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.composable.TextFieldDropdownBox

class FieldConfigScreen(val business: Business, val template: DocTemplate) : Screen {
    @Composable
    override fun Content() {
        ScaffoldWithTopBar("字段配置") { pd ->
            FieldConfigView(Modifier.padding(pd))
        }
    }
}

@Composable
fun FieldConfigView(modifier: Modifier) {
    Column(modifier) {

        Text("模版字段")
        HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))


//        Text("基本信息")
//        HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))
//
//        Row {
//            Text("fieldName")
//            Text("fieldType")
//            Text("description")
//            Text("validationRule")
//        }

        Text("业务信息")
        HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))


        Row {
            val options = mapOf(
                Pair("1", "1"),
                Pair("2", "2"),
                Pair("3", "3"),
                Pair("4", "4"),
                Pair("5", "5"),
            )
            val sl = remember { MutableList(4) { "" } }

            var v1 by remember { mutableStateOf("") }
            var v2 by remember { mutableStateOf("") }
            var v3 by remember { mutableStateOf("") }
            var v4 by remember { mutableStateOf("") }
            var v5 by remember { mutableStateOf("") }

            var fieldName by remember { mutableStateOf("") }
            val toFormFieldName: String? = null
            val fieldType: String = ""
            val description: String = ""
            val validationRule: String? = null
            val isFixed: Long = 0
            val fixedValue: String? = null

            val englishRegex = Regex("^$|^(?=.*[a-zA-Z])[a-zA-Z0-9]*$")


            TextFieldDropdownBox(options) {
//                TextFieldDropdown()
            }

//            TextFieldDropdown(options, sl, inputText = fieldName, label = "字段名", modifier = Modifier.width(150.dp)){
//                if (englishRegex.matches(it)) {
//                    fieldName = it
//                    sl[0] = fieldName
//                }
//            }
//            TextFieldDropdown(options, sl, inputText = v2, label ="字段类型", modifier = Modifier.width(150.dp)) {
//                v2 = it
//                sl[1] = it
//            }
//            TextFieldDropdown(options, sl, inputText = v3, label ="描述", modifier = Modifier.width(150.dp)) {
//                v3 = it
//                sl[2] = it
//            }
//            TextFieldDropdown(options, sl,inputText = v4,  label ="validationRule", modifier = Modifier.width(150.dp)) {
//                v4 = it
//                sl[3] = it
//            }
//            TextFieldDropdown(allOptions = listOf("是","否"),inputText = v5,  label ="是否固定值", enabled = false, modifier = Modifier.width(150.dp)) {
//                v5 = it
//            }
//            SearchableDropdown(options, sl, inputText = v5, onValueChange = { v1 = it },label ="toFormFieldName", modifier = Modifier.width(150.dp))

//            fieldName 根据模版Name字段自动生成
//            Text("fieldName")
//            Text("fieldType")
//            Text("description")
//            Text("validationRule")
        }

        Text("分组")

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