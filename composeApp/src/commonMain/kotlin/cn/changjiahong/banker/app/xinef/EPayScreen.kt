package cn.changjiahong.banker.app.xinef

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.cancel
import banker.composeapp.generated.resources.home
import banker.composeapp.generated.resources.pdf
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.PopupDialog
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.model.UserDO
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource

object EPayScreen : DirScreen {

    override val dirName: String
        get() = "信e付申请"

    @Composable
    override fun Content() = XinEFuView()


}

@Composable
fun EPayScreen.XinEFuView(ePayScreenModel: EPayScreenModel = koinScreenModel()) {
    val openAlertDialog = remember { mutableStateOf(false) }

    ePayScreenModel.handleEffect { effect ->
        when (effect) {
            is EPayEffect.CloseDialog -> {
                openAlertDialog.value = false
                true
            }
        }
        false
    }

    Row {
        Column(modifier = Modifier.width(400.dp)) {
            val navigator = LocalNavigator.currentOrThrow
            Button({
                openAlertDialog.value = true
//                EPayUIEvent.ADD_CLIENTELE.sendTo(EPayScreenModel)
            }) {
                Text("新增")
            }

            val clienteles by ePayScreenModel.clientelesData.collectAsState()
            val currentlySelected by ePayScreenModel.currentlySelected.collectAsState()
            LazyColumn {
                itemsIndexed(clienteles) { index, it ->
                    ClienteleItem(it, {
                        EPayUIEvent.SelectedClientele(it).sendTo(ePayScreenModel)
                    }, currentlySelected == it)
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xfffefbff))) {

            FoldersButton("个人征信申请材料", painterResource(Res.drawable.pdf)) {
                EPayUIEvent.GoPreTemplate("").sendTo(ePayScreenModel)
            }

        }

    }

    when {
        openAlertDialog.value -> PopupDialog(title = "新增信息", onDismissRequest = {
            openAlertDialog.value = false
        }, modifier = Modifier.width(650.dp).padding(30.dp)) {
            Column {

                val uiState by ePayScreenModel.uiState.collectAsState()


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

                    InputView(
                        label = "姓名",
                        value = uiState.username,
                        modifier = Modifier.width(200.dp).padding(10.dp, 0.dp),
                        onValueChange = { EPayUIEvent.EnterUsername(it).sendTo(ePayScreenModel) },
                        errorText = uiState.usernameError,
                        leadingIcon = painterResource(Res.drawable.home)
                    )
                    InputView(
                        label = "身份证号码",
                        value = uiState.idNumber,
                        modifier = Modifier.width(300.dp).padding(10.dp, 0.dp),
                        onValueChange = { EPayUIEvent.EnterIdNum(it).sendTo(ePayScreenModel) },
                        errorText = uiState.idNumberError,
                        leadingIcon = painterResource(Res.drawable.home)
                    )

                    InputView(
                        label = "电话号码",
                        modifier = Modifier.width(250.dp).padding(10.dp, 0.dp),
                        value = uiState.phone,
                        onValueChange = { EPayUIEvent.EnterPhone(it).sendTo(ePayScreenModel) },
                        errorText = uiState.phoneError,
                        leadingIcon = painterResource(Res.drawable.home)
                    )

                }
                Text("商户信息", modifier = Modifier.padding(10.dp, 0.dp), fontSize = 24.sp)
                HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {

                    InputView(
                        label = "经营地址",
                        modifier = Modifier.width(250.dp).padding(10.dp, 0.dp),
                        value = uiState.bAddress,
                        onValueChange = { EPayUIEvent.EnterBAddress(it).sendTo(ePayScreenModel) },
                        errorText = uiState.bAddressError,
                        leadingIcon = painterResource(Res.drawable.home)
                    )
                    InputView(
                        label = "经营范围",
                        modifier = Modifier.width(200.dp).padding(10.dp, 0.dp),
                        value = uiState.bScope,
                        onValueChange = { EPayUIEvent.EnterBScope(it).sendTo(ePayScreenModel) },
                        errorText = uiState.bScopeError,
                        leadingIcon = painterResource(Res.drawable.home)
                    )

                    InputView(
                        label = "银行卡号",
                        modifier = Modifier.width(300.dp).padding(10.dp, 0.dp),
                        value = uiState.bankerNum,
                        onValueChange = { EPayUIEvent.EnterBankerNum(it).sendTo(ePayScreenModel) },
                        errorText = uiState.bankerNumError,
                        leadingIcon = painterResource(Res.drawable.home)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        EPayUIEvent.SaveEPayDetail.sendTo(ePayScreenModel)
                    }) {
                        Text("保存")
                    }
                }
            }

        }
    }
}

@Composable
fun ClienteleItem(userDO: UserDO, onClick: () -> Unit, selected: Boolean = false) {
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
                    Text(userDO.name, modifier = Modifier.weight(1f))
                    Text(
                        formatInstantToYMD(userDO.created),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }

                Text(userDO.idNumber)

            }
        }
    }
}


fun formatInstantToYMD(instant: Instant): String {
    val zone = TimeZone.currentSystemDefault()
    val localDateTime = instant.toLocalDateTime(zone).date

    return "${localDateTime.year}年${localDateTime.monthNumber}月${localDateTime.dayOfMonth}日"
}
