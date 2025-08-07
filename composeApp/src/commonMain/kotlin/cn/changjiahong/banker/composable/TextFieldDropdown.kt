package cn.changjiahong.banker.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class Option<T>(val label: String, val value: T)

class TextFieldDropdownScope<T>(val options: List<Option<T>>) {
    val selected = mutableMapOf<String, T>()

}

@Composable
fun <T> rememberDropdownScope(options: List<Option<T>>): TextFieldDropdownScope<T> {
    return remember { TextFieldDropdownScope(options) }
}

@Composable
fun <T> rememberDropdownScope(map: Map<String, T>): TextFieldDropdownScope<T> {
    return rememberDropdownScope(map.map { (key, value) -> Option(key, value) })
}

@Composable
fun Tt() {
//    TextFieldDropdownBox(listOf()) {
//
//        TextFieldDropdown()
//    }
}


@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T> TextFieldDropdown(
    optionsScope: TextFieldDropdownScope<T>,
    selectedValue: T?,
    crossinline onValueSelected: (T?) -> Unit,
    label: String,
    enableEdit: Boolean = true,
    errorText: String = "",
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val options = optionsScope.options

    var inputText by remember(selectedValue) {
        mutableStateOf(
            selectedValue?.let { sv ->
                options.find { it.value == sv }?.label ?: ""
            } ?: ""
        )
    }

    var isFocused by remember { mutableStateOf(false) }

    val validOption = options.find { it.label == inputText }
    val id by remember { mutableStateOf(Uuid.random().toHexString()) }
    validOption?.run {
        optionsScope.selected[id] = value
    }
    val onValueSelected: (T?) -> Unit = {
        if (it == null) {
            optionsScope.selected.remove(id)
        } else {
            optionsScope.selected[id] = it
        }
        onValueSelected(it)
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        Column {
            OutlinedTextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                    expanded = true
                },
                readOnly = !enableEdit,
                textStyle = TextStyle.Default.copy(fontSize = 20.sp),
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .onFocusChanged {
                        if (!it.isFocused && isFocused) {
                            if (validOption != null) {
                                onValueSelected(validOption.value)
                            } else {
                                inputText = ""
                                onValueSelected(null)
                            }
                            expanded = false
                        }
                        isFocused = it.isFocused
                    },
                isError = inputText.isNotEmpty() && validOption == null || errorText.isNotEmpty(),
                singleLine = true
            )
            if (errorText.isNotEmpty()) {
                Text(
                    text = errorText,
                    color = Color.Red,
                    modifier = Modifier.padding(6.dp, 0.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options
                .filterNot { optionsScope.selected.containsValue(it.value) && it.value != optionsScope.selected[id] }
                .filter { it.label.contains(inputText, ignoreCase = true) || !enableEdit }
                .forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            inputText = option.label
                            onValueSelected(option.value)
                            expanded = false
                        }
                    )
                }
        }
    }
}


@Composable
fun TextFieldDropdown(
    options: List<String>,
    value: String?,
    onValueChange: (String) -> Unit,
    label: String,
    enableEdit: Boolean = true,
    errorText: String = "",
    modifier: Modifier = Modifier
) {

    val scope = rememberDropdownScope(options.map { Option(it, it) })

    TextFieldDropdown(
        optionsScope = scope,
        onValueSelected = { onValueChange(it ?: "") },
        selectedValue = value,
        label = label,
        enableEdit = enableEdit,
        errorText = errorText,
        modifier = modifier
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
inline fun BooleanFieldDropdown(
    value: Boolean,
    crossinline onValueChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (value) "是" else "否",
            onValueChange = {
                onValueChange(it == "是")
            },
            readOnly = true,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            shape = RoundedCornerShape(15.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("是", "否")
                .forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option == "是")
                            expanded = false
                        }
                    )
                }
        }
    }
}