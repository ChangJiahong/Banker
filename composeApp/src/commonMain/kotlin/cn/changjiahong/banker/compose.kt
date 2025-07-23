package cn.changjiahong.banker

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.cancel
import banker.composeapp.generated.resources.home
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TipDialog(tipText: String, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = { Button(onClick = { onDismissRequest() }) { Text("确定") } },
        title = { Text("提示") },
        text = { Text(tipText) })
}

@Composable
fun PopupDialog(
    title: String = "",
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 左侧
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {

                    }
                    // 中间
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(title)
                    }
                    // 右侧
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {

                        IconButton({ onDismissRequest() }, modifier = Modifier) {
                            Icon(
                                painter = painterResource(Res.drawable.cancel),
                                contentDescription = ""
                            )
                        }
                    }

                }
                content()
            }
        }
    }
}


@Preview
@Composable
fun FoldersButton(text: String="", icon: Painter, onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier.wrapContentHeight().width(150.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true), // 默认波纹效果
                onClick = onClick
            ).padding(5.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            if (text.isNotEmpty()) {
                Text(text, fontSize = 14.sp, modifier = Modifier.padding { paddingTop(5.dp) })
            }
        }
    }
}


@Composable
fun InputView(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    label: String = "",
    errorText: String = "",
    leadingIcon: Painter? = null,
    onValueChange: (TextFieldValue) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val isError = errorText.isNotEmpty()
    Column {
        OutlinedTextField(
            value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = if (keyboardOptions.keyboardType == KeyboardType.Password)
                PasswordVisualTransformation('*') else VisualTransformation.None,
            singleLine = true,
            leadingIcon = if (leadingIcon != null) {
                @Composable { Icon(leadingIcon, label) }
            } else null,
            modifier = modifier,
            isError = isError,
            trailingIcon = {
                if (isError) {
                    Icon(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "error",
                        tint = Color.Red
                    )
                }
            },
            shape = RoundedCornerShape(15.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = Color.Black,
//                unfocusedBorderColor = Color.Black,
//                focusedLabelColor = Color.Black
//            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isError) {
            Text(
                text = errorText,
                color = Color.Red,
                modifier = Modifier.padding(6.dp, 0.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun InputView(
    modifier: Modifier = Modifier,
    value: String = "",
    label: String = "",
    errorText: String = "",
    leadingIcon: Painter? = null,
    onValueChange: (String) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val isError = errorText.isNotEmpty()
    Column {
        OutlinedTextField(
            value,
            onValueChange = onValueChange,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = if (keyboardOptions.keyboardType == KeyboardType.Password)
                PasswordVisualTransformation('*') else VisualTransformation.None,
            singleLine = true,
            leadingIcon = if (leadingIcon != null) {
                @Composable { Icon(leadingIcon, label) }
            } else null,
            modifier = modifier,
            isError = isError,
            trailingIcon = {
                if (isError) {
                    Icon(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = "error",
                        tint = Color.Red
                    )
                }
            },
            shape = RoundedCornerShape(15.dp),
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = Color.Black,
//                unfocusedBorderColor = Color.Black,
//                focusedLabelColor = Color.Black
//            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (isError) {
            Text(
                text = errorText,
                color = Color.Red,
                modifier = Modifier.padding(6.dp, 0.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}