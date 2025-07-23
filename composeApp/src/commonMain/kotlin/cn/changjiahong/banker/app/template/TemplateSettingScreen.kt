package cn.changjiahong.banker.app.template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_box
import banker.composeapp.generated.resources.arrow_back
import banker.composeapp.generated.resources.dir
import banker.composeapp.generated.resources.pdf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.app.home.DirUiEvent
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource

object TemplateSettingScreen : Screen {
    @Composable
    override fun Content() = TemplateSettingView()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSettingScreen.TemplateSettingView(tempSettingScreenModel: TemplateSettingScreenModel = koinScreenModel()) {

    val globalNavigator =
        GlobalNavigator.current
    Scaffold(
        topBar = {
            TopAppBar({
                Text("模版文件维护")
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
        TempFileGridView(modifier = Modifier.padding(paddingValues), tempSettingScreenModel)
    }
}

@Composable
fun TempFileGridView(
    modifier: Modifier = Modifier,
    tempSettingScreenModel: TemplateSettingScreenModel
) {

    val tempFiles by tempSettingScreenModel.tempFiles.collectAsState()
    LazyVerticalGrid(
        GridCells.FixedSize(100.dp), contentPadding = PaddingValues(5.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(tempFiles) { index, item ->
            FoldersButton(item.templateName, painterResource(Res.drawable.pdf)) {
                TempSettingUiEvent.GoPreTemplateScreen(item).sendTo(tempSettingScreenModel)
            }
        }

        item {
            FoldersButton(icon = painterResource(Res.drawable.add_box)) {

            }
        }
    }
}