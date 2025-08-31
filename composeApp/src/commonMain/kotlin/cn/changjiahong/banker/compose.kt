package cn.changjiahong.banker

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.arrow_back
import banker.composeapp.generated.resources.cancel
import banker.composeapp.generated.resources.dir
import banker.composeapp.generated.resources.excel
import banker.composeapp.generated.resources.home
import banker.composeapp.generated.resources.pdf
import banker.composeapp.generated.resources.unknown_file
import banker.composeapp.generated.resources.word
import cn.changjiahong.banker.app.about.settings.business.BusinessGridView
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.model.UserInfo
import cn.changjiahong.banker.storage.FileType
import cn.changjiahong.banker.utils.padding
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ClienteleItem(userDO: UserInfo, onClick: () -> Unit, selected: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        RadioButton(
            selected = selected,
            onClick = onClick // null recommended for accessibility with screen readers
        )

        Card(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xfff2f0f4)),
            onClick = onClick
        ) {
            Column(Modifier.padding(10.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    val name = userDO.fields["姓名"]?.fieldValue
                    Text(name?:"未知", modifier = Modifier.weight(1f))
//                    Text(
//                        formatInstantToYMD(userDO.created),
//                        modifier = Modifier.weight(1f),
//                        textAlign = TextAlign.End
//                    )
                }
                val idNum = userDO.fields["证件号码"]?.fieldValue

                Text(idNum?:"null")

            }
        }
    }
}


fun formatInstantToYMD(instant: Instant): String {
    val zone = TimeZone.currentSystemDefault()
    val localDateTime = instant.toLocalDateTime(zone).date

    return "${localDateTime.year}年${localDateTime.monthNumber}月${localDateTime.dayOfMonth}日"
}


@Composable
fun RightClickMenu(
    menu: @Composable ColumnScope.(close: () -> Unit) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(DpOffset(0.dp, 0.dp)) }
    Box(
        modifier = Modifier
            .wrapContentSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val mouseEvent = event.buttons

                        if (mouseEvent.isSecondaryPressed) {
                            val position = event.changes.first().position
                            offset = DpOffset(position.x.toDp(), position.y.toDp())
                            expanded = true
                        }
                    }
                }
            }) {


        Box {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = offset
            ) {
                menu {
                    expanded = false
                }
            }

        }

        content()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    // Customization options
    placeholder: @Composable () -> Unit = { Text("Search") },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Track expanded state of search bar
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = { },
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon
                )
            },
            expanded = expanded,
            onExpandedChange = { },
        ) {
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBar(
    title: String,
    iconPainter: Painter? = null,
    iconDescription: String = "",
    iconOnClick: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val globalNavigator =
        GlobalNavigator.current
    Scaffold(
        topBar = {
            TopAppBar({
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title)
                    iconPainter?.run {
                        IconButton(onClick = iconOnClick, modifier = Modifier) {
                            Icon(
                                painter = iconPainter,
                                contentDescription = iconDescription
                            )
                        }
                    }

                }
            }, navigationIcon = {
                IconButton(onClick = {
                    globalNavigator.pop()
                }, modifier = Modifier) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = "arrow back"
                    )
                }
            })
        }
    ) { paddingValues ->
        content(paddingValues)
    }
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
fun FoldersButton(
    text: String = "",
    fileType: String = FileType.DIR,
    icon: Painter? = null,
    onClick: () -> Unit = {}
) {
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
                painter = icon ?: painterResource(
                    when (FileType.getFileType(fileType)) {
                        FileType.PDF -> Res.drawable.pdf
                        FileType.DIR -> Res.drawable.dir
                        FileType.DOC, FileType.DOCX -> Res.drawable.word
                        FileType.XLS, FileType.XLSX -> Res.drawable.excel
                        else -> Res.drawable.unknown_file
                    }
                ),
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
    readOnly: Boolean = false,
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
            readOnly = readOnly,
            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = TextStyle.Default.copy(fontSize = 20.sp),
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