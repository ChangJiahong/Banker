package cn.changjiahong.banker.app.business_handle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.ClienteleItem
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.composable.AlertDialogState
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.model.FieldVal
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parameterArrayOf

class BusinessHandlerScreen(val business: Business) : DirScreen {
    override val dirName: String
        get() = business.businessName

    @Composable
    override fun Content() = BusinessHandlerView()

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessHandlerScreen.BusinessHandlerView(
    businessHandlerScreenModel: BusinessHandlerScreenModel = koinScreenModel(
        parameters = { parameterArrayOf(business) }
    )
) {
    val openAlertDialog = businessHandlerScreenModel.clienteleDialog
    val openOtherSoftwareDialog = businessHandlerScreenModel.openOtherSoftwareDialog

    val uiState by businessHandlerScreenModel.uiState.collectAsState()

    businessHandlerScreenModel.handleEffect { effect ->
        when (effect) {

        }
        false
    }

    Row {
        Column(modifier = Modifier.width(400.dp)) {
            val navigator = LocalNavigator.currentOrThrow
            Row {

                Button({
                    BhUIEvent.NewClientele.sendTo(businessHandlerScreenModel)
                }) {
                    Text("新增")
                }
                Button({
                    BhUIEvent.EditClientele.sendTo(businessHandlerScreenModel)
                }) {
                    Text("编辑")
                }
            }

            val clienteles by businessHandlerScreenModel.clientelesData.collectAsState()
            val currentlySelected by businessHandlerScreenModel.currentlySelected.collectAsState()
            LazyColumn {
                itemsIndexed(clienteles) { index, it ->
                    ClienteleItem(it, {
                        BhUIEvent.SelectedClientele(it).sendTo(businessHandlerScreenModel)
                    }, currentlySelected == it)
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xfffefbff))) {

            val templates by businessHandlerScreenModel.templatesData.collectAsState()
            val currentlySelected by businessHandlerScreenModel.currentlySelected.collectAsState()

            LazyVerticalGrid(
                GridCells.FixedSize(100.dp),
                contentPadding = PaddingValues(5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
            ) {
                itemsIndexed(templates) { index, it ->
                    FoldersButton(it.templateName, fileType = it.fileType) {
                        BhUIEvent.ClickTplItem(it)
                            .sendTo(businessHandlerScreenModel)
                    }
                }
            }
        }

    }

    ClienteleDialog(businessHandlerScreenModel, openAlertDialog)
    OpenOtherSoftwareDialog(businessHandlerScreenModel,openOtherSoftwareDialog)

}

@Composable
fun OpenOtherSoftwareDialog(
    businessHandlerScreenModel: BusinessHandlerScreenModel,dialogState: AlertDialogState) {
    val visible by dialogState.visible.collectAsState()
    if (!visible) {
        return
    }
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(onClick = {
                dialogState.ok()
            }) { Text("确定") }
        },
        dismissButton = {
            Button(onClick = {
                dialogState.dismiss()
            }) { Text("取消") }
        },
        title = { Text("提示") },
        text = { Text("暂不支持该类型文件预览，是否跳转系统默认软件打开") })


}


@Composable
fun ClienteleDialog(
    businessHandlerScreenModel: BusinessHandlerScreenModel,
    dialogState: DialogState
) {
    cn.changjiahong.banker.composable.PopupDialog(
        title = "新增信息",
        popupDialogState = dialogState,
        modifier = Modifier.width(850.dp).fillMaxHeight().padding(30.dp)
    ) {
        Column {

            val uiState by businessHandlerScreenModel.uiState.collectAsState()

            val fieldValues by businessHandlerScreenModel.fieldValues.collectAsState()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("基本信息", modifier = Modifier.padding(10.dp, 0.dp), fontSize = 24.sp)
                IconButton(onClick = {}, modifier = Modifier) {
                    Icon(painterResource(Res.drawable.home), contentDescription = "")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))
            FlowRow(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ) {
                val basicFields by businessHandlerScreenModel.basicFields.collectAsState()

                basicFields.forEachIndexed { index, field ->

                    var fieldVal by remember {
                        mutableStateOf(
                            fieldValues[field.fieldId] ?: FieldVal(
                                field.fieldId,
                            )
                        )
                    }

                    InputView(
                        label = field.alias,
                        value = fieldVal.fieldValue,
                        modifier = Modifier.width(200.dp).padding(10.dp, 0.dp),
                        onValueChange = { newValue ->
                            fieldVal = fieldVal.copy(fieldValue = newValue)
                            BhUIEvent.UpdateFieldValue(
                                field.fieldId,
                                fieldVal
                            ).sendTo(businessHandlerScreenModel)

                        },
//                        errorText = uiState.usernameError,
                        leadingIcon = painterResource(Res.drawable.home)
                    )

                }


            }

            val businessFields by businessHandlerScreenModel.businessFields.collectAsState()

            Text(
                "业务信息",
                modifier = Modifier.padding(10.dp, 0.dp),
                fontSize = 24.sp
            )
            HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))
            FlowRow(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ) {

                businessFields.forEach { field ->
                    var fieldVal by remember {
                        mutableStateOf(
                            fieldValues[field.fieldId] ?: FieldVal(field.fieldId)
                        )
                    }
                    InputView(
                        label = field.alias,
                        modifier = Modifier.width(250.dp).padding(10.dp, 0.dp),
                        value = fieldVal.fieldValue,
                        onValueChange = {
                            fieldVal = fieldVal.copy(fieldValue = it)
                            BhUIEvent.UpdateFieldValue(
                                field.fieldId,
                                fieldVal
                            ).sendTo(businessHandlerScreenModel)

                        },
                        errorText = uiState.fieldErrorMsg[field.fieldName] ?: "",
                        leadingIcon = painterResource(Res.drawable.home)
                    )
                }
            }

//
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    BhUIEvent.SaveBhDetail.sendTo(businessHandlerScreenModel)
                }) {
                    Text("保存")
                }
            }
        }
    }
}