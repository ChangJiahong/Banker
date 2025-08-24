package cn.changjiahong.banker.app.about.settings.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_box
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.RightClickMenu
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.composable.PopupDialog
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.composable.RoundedInputField
import cn.changjiahong.banker.composable.rememberDialogState
import cn.changjiahong.banker.storage.FileType
import cn.changjiahong.banker.uieffect.Toast
import cn.changjiahong.banker.utils.padding
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import org.jetbrains.compose.resources.painterResource

object TemplateSettingScreen : Screen {
    @Composable
    override fun Content() = TemplateSettingView()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSettingScreen.TemplateSettingView(tempSettingScreenModel: TemplateSettingScreenModel = koinScreenModel()) {

    ScaffoldWithTopBar("模版文件维护") { paddingValues ->
        TempFileGridView(modifier = Modifier.padding(paddingValues), tempSettingScreenModel)
    }
}

@Composable
fun TempFileGridView(
    modifier: Modifier = Modifier,
    tempSettingScreenModel: TemplateSettingScreenModel
) {

    val popupDialogState = rememberDialogState()

    val tempFiles by tempSettingScreenModel.tempFiles.collectAsState()
    LazyVerticalGrid(
        GridCells.FixedSize(100.dp), contentPadding = PaddingValues(5.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(tempFiles) { index, item ->
            RightClickMenu(menu = {
                DropdownMenuItem(text = {
                    Text("预览")
                }, onClick = {
                    TempSettingUiEvent.GoPreTemplateScreen(item).sendTo(tempSettingScreenModel)
                    it()
                })
                DropdownMenuItem(text = {
                    Text("查看表单")
                }, onClick = {
                    TempSettingUiEvent.GoTempFiledSettingScreen(item).sendTo(tempSettingScreenModel)
                    it()
                })
            }) {
                FoldersButton(item.templateName, fileType = item.fileType) {
                    TempSettingUiEvent.GoPreTemplateScreen(item).sendTo(tempSettingScreenModel)
                }
            }
        }

        item {
            FoldersButton(icon = painterResource(Res.drawable.add_box)) {
                popupDialogState.show()
            }
        }
    }
    AddDocTemplate(popupDialogState, tempSettingScreenModel)
}

@Composable
fun AddDocTemplate(
    popupDialogState: DialogState,
    tempSettingScreenModel: TemplateSettingScreenModel
) {
    tempSettingScreenModel.handleEffect {
        when(it){
            is TempSettingUiEffect.AddTempSuccess ->{
                popupDialogState.dismiss()
                true
            }
            else -> false
        }
    }
    PopupDialog(popupDialogState, title = "添加模版文件", modifier = Modifier.fillMaxWidth(0.6f)) {
        var filePath by remember { mutableStateOf("") }
        var selectFile by remember { mutableStateOf<PlatformFile?>(null) }

        val launcher = rememberFilePickerLauncher(type = FileKitType.File(FileType.SupportedType)) { file ->
            // Handle the file
            println(file?.path)
            selectFile = file
            filePath = file?.path ?: ""
        }

        Column(
            Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RoundedInputField(
                filePath,
                { filePath = it },
                readOnly = true,
                modifier = Modifier.padding { paddingBottom(10.dp) },
                trailingIcon = {
                    IconButton({
                        launcher.launch()
                    }) {
                        Icon(painterResource(Res.drawable.home), contentDescription = "")
                    }
                })

            Button({
                if (selectFile == null) {
                    Toast("请选择一个文件").sendTo(tempSettingScreenModel)
                    return@Button
                }
                TempSettingUiEvent.AddDocTemplate(selectFile!!).sendTo(tempSettingScreenModel)
            }) {
                Text("添加")
            }

        }
    }
}