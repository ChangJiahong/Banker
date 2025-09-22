package cn.changjiahong.banker.composable

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 *
 * @author ChangJiahong
 * @date 2025/9/11
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchDropdown(
    modifier: Modifier = Modifier,
    selectedValue: T?,
    searchResults: List<Option<T>>,
    onResultSelected: (T?) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    label: String,
    errorText: String,
) {
    var expanded by remember { mutableStateOf(false) }
    var inputText by remember(selectedValue) {
        mutableStateOf(
            selectedValue?.let { sv ->
                searchResults.find { it.value == sv }?.label ?: ""
            } ?: ""
        )
    }
    var isFocused by remember { mutableStateOf(false) }
    val validOption = searchResults.find { it.label == inputText }

    val focusRequester = remember { FocusRequester() }

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
                    onQueryChange(it)
                    expanded = true
                },
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions {
                    onSearch(inputText)
                    focusRequester.requestFocus()
                    expanded=true
                },
                suffix =
                    @Composable {
                        IconButton({
                            onSearch(inputText)
                            focusRequester.requestFocus()
                            expanded=true
                        },Modifier.size(24.dp)) {
                            Icon(Icons.Default.Search, null)
                        }
                    },
                prefix = {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowRight, null,
                        Modifier.rotate(if (expanded) 90f else 0f)
                    )
                },
                trailingIcon = if (errorText.isNotEmpty()) {
                    @Composable {
                        val s = rememberTooltipState()
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip { Text(errorText) }
                            },
                            state = s
                        ) {
                            Icon(Icons.Default.Error, contentDescription = errorText)
                        }
                    }
                } else null,
                modifier = Modifier.focusRequester(focusRequester)
                    .menuAnchor(MenuAnchorType.PrimaryEditable, true)
                    .onFocusChanged {
                        if (!it.isFocused && isFocused) {
                            if (validOption != null) {
                                onResultSelected(validOption.value)
                            } else {
                                inputText = ""
                                onResultSelected(null)
                            }
                            expanded = false
                        }
                        isFocused = it.isFocused
                    },
                isError = inputText.isNotEmpty() && validOption == null || errorText.isNotEmpty(),
                singleLine = true
            )
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            searchResults
                .filter { it.label.contains(inputText, ignoreCase = true) }
                .forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.label) },
                        onClick = {
                            inputText = option.label
                            onResultSelected(option.value)
                            expanded = false
                        }
                    )
                }
        }
    }
}