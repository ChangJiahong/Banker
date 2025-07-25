package cn.changjiahong.banker.composable

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TextFieldDropdownScope<T>(val options: Map<String, T>) {
    val selected = mapOf<String, T>()

}


@Composable
fun Tt() {
    TextFieldDropdownBox(mapOf(Pair("", ""))) {
//        TextFieldDropdown()
    }
}

@Composable
fun <T> TextFieldDropdownBox(
    options: Map<String, T>,
    content: @Composable TextFieldDropdownScope<T>.() -> Unit
) {

    val textFieldDropdownScope = TextFieldDropdownScope(options)

    textFieldDropdownScope.content()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
inline fun <reified T> TextFieldDropdownScope<T>.TextFieldDropdown(
    value: T,
    crossinline onValueChange: (T) -> Unit,
    label: String,
    enableEdit: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val id by remember { mutableStateOf(Uuid.random().toHexString()) }

    val vToStr: (T) -> String =
        { v -> options.filterValues { it == v }.firstNotNullOfOrNull { it.key } ?: "" }
    val strToV: (String) -> T? = { k -> options[k] }

    var inputText by rememberSaveable { mutableStateOf(vToStr(value)) }

    var allowEdit = enableEdit
    if (T::class != String::class) {
        allowEdit = false
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
//        var inputText by remember {
//            mutableStateOf(valueTransform(value))
//        }

        OutlinedTextField(
            value = inputText,
            onValueChange = {
                inputText = it
                val vv = strToV(it)
                if (vv != null) {
                    onValueChange(vv)
                } else if (T::class != String::class) {
                    onValueChange(it as T)
                }
            },
            readOnly = allowEdit,
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
            options
//                .filterNot { it in selected }
//                .filter { it.contains(inputText, ignoreCase = true) }
                .forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.key) },
                        onClick = {
//                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
        }
    }
}