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

class TextFieldDropdownScope(val options: List<String>) {
    val selected = mutableMapOf<String, String>()

}


//@Composable
//fun Tt() {
////    TextFieldDropdownBox(mapOf(Pair("", ""))) {
////        TextFieldDropdown()
//    }
//}

@Composable
fun TextFieldDropdownBox(
    options: List<String>,
    content: @Composable TextFieldDropdownScope.() -> Unit
) {

    val textFieldDropdownScope = TextFieldDropdownScope(options)

    textFieldDropdownScope.content()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
inline fun TextFieldDropdownScope.TextFieldDropdown(
    value: String,
    crossinline onValueChange: (String) -> Unit,
    label: String,
    enableEdit: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val id by remember { mutableStateOf(Uuid.random().toHexString()) }

    val onValueChange: (String) -> Unit = {
        selected[id] = it
        onValueChange(it)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            readOnly = !enableEdit,
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
                .filterNot { selected.containsValue(it) }
                .filter { it.contains(value, ignoreCase = true) }
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