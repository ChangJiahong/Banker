package cn.changjiahong.banker.app.about.settings.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.DropdownMenuItem
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
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.template.RightClickMenu
import cn.changjiahong.banker.app.about.settings.template.TempFileGridView
import cn.changjiahong.banker.app.about.settings.template.TempSettingUiEvent
import cn.changjiahong.banker.app.about.settings.template.TemplateSettingScreenModel
import org.jetbrains.compose.resources.painterResource


object BusinessSettingScreen : Screen {
    @Composable
    override fun Content() = BusinessSettingView()
}

@Composable
fun BusinessSettingScreen.BusinessSettingView(businessSettingScreenModel:BusinessSettingScreenModel = koinScreenModel()) {

    ScaffoldWithTopBar("业务配置"){paddingValues->
        BusinessGridView(modifier = Modifier.padding(paddingValues), businessSettingScreenModel)
    }
}



@Composable
fun BusinessGridView(
    modifier: Modifier = Modifier,
    businessSettingScreenModel: BusinessSettingScreenModel
) {

    val businessList by businessSettingScreenModel.businessList.collectAsState()
    LazyVerticalGrid(
        GridCells.FixedSize(100.dp), contentPadding = PaddingValues(5.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(businessList) { index, item ->
            RightClickMenu(menu = {
                DropdownMenuItem(text = {
                    Text("预览")
                }, onClick = {
                    it()
                })
                DropdownMenuItem(text = {
                    Text("查看表单")
                }, onClick = {
                    println("菜单项2点击")
                    it()
                })
            }) {
                FoldersButton(item.businessName, painterResource(Res.drawable.dir)) {
                    BusinessSettingsUiEvent.GoBusinessTmpDetails(item).sendTo(businessSettingScreenModel)
                }
            }
        }

        item {
            FoldersButton(icon = painterResource(Res.drawable.add_box)) {

            }
        }
    }
}