package cn.changjiahong.banker.app.about.settings.business

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.RightClickMenu
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.composable.PopupDialog
import cn.changjiahong.banker.composable.VisibleState
import cn.changjiahong.banker.composable.RoundedInputField
import cn.changjiahong.banker.composable.rememberVisibleState
import cn.changjiahong.banker.model.Biz
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource


object BusinessSettingScreen : Screen {
    @Composable
    override fun Content() = BusinessSettingView()
}

@Composable
fun BusinessSettingScreen.BusinessSettingView(businessSettingScreenModel: BusinessSettingScreenModel = koinScreenModel()) {

    ScaffoldWithTopBar("业务配置") { paddingValues ->
        BusinessGridView(modifier = Modifier.padding(paddingValues), businessSettingScreenModel)
    }
}


@Composable
fun BusinessGridView(
    modifier: Modifier = Modifier,
    businessSettingScreenModel: BusinessSettingScreenModel
) {
    val popupDialogState = rememberVisibleState()

    val businessList by businessSettingScreenModel.businessList.collectAsState()

    LazyVerticalGrid(
        GridCells.FixedSize(100.dp), contentPadding = PaddingValues(5.dp),
        modifier = modifier,
        horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.Center
    ) {
        itemsIndexed(businessList) { index, item ->
            RightClickMenu(menu = {
                DropdownMenuItem(text = {
                    Text("修改")
                }, onClick = {
                    BusinessSettingsUiEvent.ShowUpdateDialog(Biz(item.id, item.businessName))
                        .sendTo(businessSettingScreenModel)
                    it()
                })
            }) {
                FoldersButton(item.businessName) {
                    BusinessSettingsUiEvent.GoBusinessTmpDetails(item)
                        .sendTo(businessSettingScreenModel)
                }
            }
        }

        item {
            FoldersButton(icon = painterResource(Res.drawable.add_box)) {
                BusinessSettingsUiEvent.ShowAddDialog
                    .sendTo(businessSettingScreenModel)
            }
        }
    }
    AddBusinessDialog(popupDialogState, businessSettingScreenModel)
}


@Composable
fun AddBusinessDialog(
    popupVisibleState: VisibleState,
    businessSettingScreenModel: BusinessSettingScreenModel
) {
    val popupDialogState = businessSettingScreenModel.popupVisibleState

    val dialogBiz by businessSettingScreenModel.dialogBiz.collectAsState()

    PopupDialog(
        popupDialogState,
        title = "${if (dialogBiz.id < 0) "添加" else "修改"}模版",
        Modifier.fillMaxWidth(0.6f)
    ) {

        var queryS by remember(dialogBiz) { mutableStateOf(dialogBiz.name) }

        Column(
            Modifier.fillMaxWidth().padding(10.dp).wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            RoundedInputField(
                queryS,
                {
                    queryS = it
                    BusinessSettingsUiEvent.UpdateBusiness(dialogBiz.copy(name = it))
                        .sendTo(businessSettingScreenModel)
                },
                onGo = {
                    BusinessSettingsUiEvent.SaveBusiness.sendTo(businessSettingScreenModel)
                },
                autoFocus = true,
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            Button({
                BusinessSettingsUiEvent.SaveBusiness.sendTo(businessSettingScreenModel)
            }, modifier = Modifier.fillMaxWidth(0.2f).padding { paddingVertical(5.dp) }) {
                Text("确定")
            }
        }

    }
}