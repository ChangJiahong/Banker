package cn.changjiahong.banker.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    imeAction: ImeAction = ImeAction.Search,
    onGo: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    autoFocus: Boolean = false,
    hint: String = "",
    cornerRadius: Dp = 26.dp,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }

    if (autoFocus) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint) },
        readOnly = readOnly,
        modifier = modifier.focusRequester(focusRequester),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = Color(0xffddd5e2),
            unfocusedContainerColor = Color(0xffddd5e2)
        ),
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = KeyboardActions(onSearch = { onGo(value) }),
        shape = RoundedCornerShape(cornerRadius),
        singleLine = true,
        trailingIcon = trailingIcon,
        leadingIcon = leadingIcon
    )
}