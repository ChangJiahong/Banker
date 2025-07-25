package cn.changjiahong.banker.app.about.settings.business.tmp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_box
import banker.composeapp.generated.resources.dir
import banker.composeapp.generated.resources.pdf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.business.BusinessSettingsUiEvent
import cn.changjiahong.banker.app.about.settings.template.RightClickMenu
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parameterArrayOf

class BusinessTmpDetailScreen(val business: Business): Screen {
    @Composable
    override fun Content() {
        ScaffoldWithTopBar("模版配置") { paddingValues ->
            BusinessTmpDetailView(Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun BusinessTmpDetailScreen.BusinessTmpDetailView(modifier: Modifier) {
    val businessTmpDetailScreenModel = koinScreenModel<BusinessTmpDetailScreenModel> {  parameterArrayOf(business) }

    val tmpDetails by businessTmpDetailScreenModel.tmpDetails.collectAsState()
    LazyVerticalGrid(
        GridCells.FixedSize(100.dp), contentPadding = PaddingValues(5.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(tmpDetails) { index, item ->
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
                FoldersButton(item.templateName, painterResource(Res.drawable.pdf)) {
                    BusinessTmpDetailUiEvent.GoFieldConfigScreen(business,item).sendTo(businessTmpDetailScreenModel)
//                    BusinessSettingsUiEvent.GoBusinessTmpDetails(item).sendTo(businessTmpDetailScreenModel)
                }
            }
        }

        item {
            FoldersButton(icon = painterResource(Res.drawable.add_box)) {

            }
        }
    }
}