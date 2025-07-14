package cn.changjiahong.banker.app.business_handle

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import banker.composeapp.generated.resources.pdf
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.app.xinef.ClienteleItem
import cn.changjiahong.banker.app.xinef.EPayUIEvent
import cn.changjiahong.banker.app.xinef.PopupDialog
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
    val openAlertDialog = remember { mutableStateOf(false) }

    val uiState by businessHandlerScreenModel.uiState.collectAsState()

    businessHandlerScreenModel.handleEffect { effect ->
        when (effect) {
            is BhEffect.CloseDialog -> {
                openAlertDialog.value = false
                true
            }

            is BhEffect.OpenDialog -> {
                openAlertDialog.value = true
            }
        }
        false
    }

    Row {
        Column(modifier = Modifier.width(400.dp)) {
            val navigator = LocalNavigator.currentOrThrow
            Button({
                BhUIEvent.AddClientele.sendTo(businessHandlerScreenModel)
            }) {
                Text("新增")
            }

            val clienteles by businessHandlerScreenModel.clientelesData.collectAsState()
            val currentlySelected by businessHandlerScreenModel.currentlySelected.collectAsState()
            LazyColumn {
                itemsIndexed(clienteles) { index, it ->
                    ClienteleItem(it, {
                        EPayUIEvent.SelectedClientele(it).sendTo(businessHandlerScreenModel)
                    }, currentlySelected == it)
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xfffefbff))) {

            FoldersButton("个人征信申请材料", painterResource(Res.drawable.pdf)) {
                EPayUIEvent.GoPreTemplate("").sendTo(businessHandlerScreenModel)
            }

        }

    }

    when {
        openAlertDialog.value -> {
            val businessWithFields = uiState.businessFields
            if (businessWithFields != null) {
                AddClienteleDialog(businessHandlerScreenModel) {
                    openAlertDialog.value = false
                }
            } else {
                BasicAlertDialog(onDismissRequest = { openAlertDialog.value = false }) {
                    Text("错误")
                }
            }
        }
    }
}

@Composable
fun AddClienteleDialog(
    businessHandlerScreenModel: BusinessHandlerScreenModel,
    onDismissRequest: () -> Unit
) {

    PopupDialog(
        title = "新增信息",
        onDismissRequest = onDismissRequest,
        modifier = Modifier.width(650.dp).padding(30.dp)
    ) {
//        Column {
//
//            val uiState by businessHandlerScreenModel.uiState.collectAsState()
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text("基本信息", modifier = Modifier.padding(10.dp, 0.dp), fontSize = 24.sp)
//                IconButton(onClick = {}, modifier = Modifier) {
//                    Icon(painterResource(Res.drawable.home), contentDescription = "")
//                }
//            }
//            HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))
//            FlowRow(
//                verticalArrangement = Arrangement.Center,
//                modifier = Modifier.fillMaxWidth().padding(10.dp)
//            ) {
//                val uiState by businessHandlerScreenModel.uiState.collectAsState()
//
//                var usernameFieldValue by remember(uiState.username) {
//                    mutableStateOf(TextFieldValue(uiState.username))
//                }
//
//                LaunchedEffect(uiState.username) {
//                    if (usernameFieldValue.text != uiState.username && usernameFieldValue.composition == null) {
//                        usernameFieldValue = TextFieldValue(uiState.username)
//                    }
//                }
//
//
//                InputView(
//                    label = "姓名",
//                    value = usernameFieldValue,
//                    modifier = Modifier.width(200.dp).padding(10.dp, 0.dp),
//                    onValueChange = {newValue->
//                        usernameFieldValue = newValue
//                        if (newValue.composition == null) {
////                            businessHandlerScreenModel.handleEvent(BhUIEvent.EnterUsername(newValue.text))
//                            BhUIEvent.EnterUsername(newValue.text).sendTo(businessHandlerScreenModel)
//                        }
//
//                    },
//                    errorText = uiState.usernameError,
//                    leadingIcon = painterResource(Res.drawable.home)
//                )
//                InputView(
//                    label = "身份证号码",
//                    value = uiState.idNumber,
//                    modifier = Modifier.width(300.dp).padding(10.dp, 0.dp),
//                    onValueChange = {
//                        BhUIEvent.EnterIdNum(it).sendTo(businessHandlerScreenModel)
//                    },
//                    errorText = uiState.idNumberError,
//                    leadingIcon = painterResource(Res.drawable.home)
//                )
//
//                InputView(
//                    label = "电话号码",
//                    modifier = Modifier.width(250.dp).padding(10.dp, 0.dp),
//                    value = uiState.phone,
//                    onValueChange = {
//                        BhUIEvent.EnterPhone(it).sendTo(businessHandlerScreenModel)
//                    },
//                    errorText = uiState.phoneError,
//                    leadingIcon = painterResource(Res.drawable.home)
//                )
//
//            }
//
////            val businessFields = uiState.businessFields
////
////            businessFields.fieldGroups.forEach { businessFieldGroup ->
//////                Text(
//////                    businessFieldGroup.groupName,
//////                    modifier = Modifier.padding(10.dp, 0.dp),
//////                    fontSize = 24.sp
//////                )
//////                HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))
////                FlowRow(
////                    verticalArrangement = Arrangement.Center,
////                    modifier = Modifier.fillMaxWidth().padding(10.dp)
////                ) {
////
////                    businessFieldGroup.fields.forEach { field ->
////                        InputView(
////                            label = field.description,
////                            modifier = Modifier.width(250.dp).padding(10.dp, 0.dp),
////                            value = uiState.fieldValues[field.id] ?: "",
////                            onValueChange = {
////                                BhUIEvent.EnterField(
////                                    field.id,
////                                    it
////                                ).sendTo(businessHandlerScreenModel)
////
////                            },
////                            errorText = uiState.fieldErrorMsg[field.fieldName] ?: "",
////                            leadingIcon = painterResource(Res.drawable.home)
////                        )
////                    }
////                }
////            }
////
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
//        }

        Column {
            val uiState by businessHandlerScreenModel.uiState.collectAsState()
            var t by remember { mutableStateOf("") }
            InputView(
                label = "电话号码",
                modifier = Modifier.width(250.dp).padding(10.dp, 0.dp),
                value = t,
                onValueChange = {
                    t = it
                },
            )
            // 用 rememberSaveable 保留状态和光标信息
            var username by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                mutableStateOf(TextFieldValue())
            }

            InputView(
                label = "电话号码",
                modifier = Modifier.width(250.dp).padding(10.dp, 0.dp),
                value = username,
                onValueChange = {
                    username=it
                    BhUIEvent.EnterUsername(it.text).sendTo(businessHandlerScreenModel)
                },
            )
        }
    }
}