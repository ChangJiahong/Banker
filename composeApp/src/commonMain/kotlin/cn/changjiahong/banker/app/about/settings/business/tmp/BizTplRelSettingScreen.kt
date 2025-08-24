package cn.changjiahong.banker.app.about.settings.business.tmp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.add_box
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.RightClickMenu
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.composable.PopupDialog
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.composable.RoundedInputField
import cn.changjiahong.banker.composable.rememberDialogState
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parameterArrayOf

class BusinessTmpDetailScreen(val business: Business) : Screen {
    @Composable
    override fun Content() {
        val businessTmpDetailScreenModel =
            koinScreenModel<BusinessTmpDetailScreenModel> { parameterArrayOf(business) }

        ScaffoldWithTopBar(
            "模版配置",
            iconPainter = painterResource(Res.drawable.home),
            iconOnClick = {
                BusinessTmpDetailUiEvent.GoBusinessFieldConfigScreen(business)
                    .sendTo(businessTmpDetailScreenModel)
            }) { paddingValues ->
            BusinessTmpDetailView(Modifier.padding(paddingValues), businessTmpDetailScreenModel)
        }
    }
}

@Composable
fun BusinessTmpDetailScreen.BusinessTmpDetailView(
    modifier: Modifier,
    businessTmpDetailScreenModel: BusinessTmpDetailScreenModel
) {
    val popupDialogState = rememberDialogState()

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
                FoldersButton(item.templateName, fileType = item.fileType) {
                    BusinessTmpDetailUiEvent.GoTempFieldConfigScreen(business, item)
                        .sendTo(businessTmpDetailScreenModel)
//                    BusinessSettingsUiEvent.GoBusinessTmpDetails(item).sendTo(businessTmpDetailScreenModel)
                }
            }
        }

        item {
            //添加
            FoldersButton(icon = painterResource(Res.drawable.add_box)) {
                popupDialogState.show()
            }
        }
    }
    AddTemplateDialog(popupDialogState, businessTmpDetailScreenModel)

}

@Composable
fun AddTemplateDialog(
    popupDialogState: DialogState,
    businessTmpDetailScreenModel: BusinessTmpDetailScreenModel
) {
    businessTmpDetailScreenModel.handleEffect {
        when (it) {
            is BusinessTmpDetailUiEffect.AddTempSuccess -> {
                popupDialogState.dismiss()
                true
            }

            else -> false
        }
    }

    PopupDialog(popupDialogState, title = "添加模版", Modifier.fillMaxSize(0.8f)) {

        var se by remember { mutableStateOf("") }

        val textFieldState = rememberTextFieldState()

        val searchRes by businessTmpDetailScreenModel.searchRes.collectAsState()

//        SimpleSearchBar(textFieldState, { println(it) }, searchResults = listOf("aaa", "bb"))

        var queryS by remember { mutableStateOf("") }

        val lis = mutableListOf("a", "v")

        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

            RoundedInputField(
                queryS,
                onValueChange = { queryS = it },
                imeAction = ImeAction.Search,
                onGo = {
                    BusinessTmpDetailUiEvent.FuzzySearch(queryS)
                        .sendTo(businessTmpDetailScreenModel)
                },
                trailingIcon = {
                    IconButton({
                        BusinessTmpDetailUiEvent.FuzzySearch(queryS)
                            .sendTo(businessTmpDetailScreenModel)
                    }) {
                        Icon(
                            painterResource(Res.drawable.home),
                            contentDescription = "Search"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            )

            LazyColumn(modifier = Modifier) {
                items(searchRes) { temp ->
                    Row {

                        Text(temp.templateName, modifier = Modifier.clickable(onClick = {
                            BusinessTmpDetailUiEvent.AddTemplate(temp)
                                .sendTo(businessTmpDetailScreenModel)
                        }))
                    }
                }
            }
        }

    }
}
