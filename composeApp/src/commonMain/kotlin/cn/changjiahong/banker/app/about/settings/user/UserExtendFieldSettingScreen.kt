package cn.changjiahong.banker.app.about.settings.user

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.platform.HorizontalScrollbar
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource

class UserExtendFieldSettingScreen : Screen {

    @Composable
    override fun Content() {

        ScaffoldWithTopBar("客户扩展信息") { pd ->
            ExtendFieldSettingView(Modifier.padding(pd))
        }
    }
}

@Composable
private fun UserExtendFieldSettingScreen.ExtendFieldSettingView(modifier: Modifier) {
    val fieldConfigScreenModel = koinScreenModel<UserExtendFieldSettingScreenModel>()

    Column(modifier) {
        val uFields by fieldConfigScreenModel.uFiledConfigs.collectAsState()
        val uFiledErrors by fieldConfigScreenModel.uFiledErrors.collectAsState()

        Row(Modifier.padding {
            paddingVertical(5.dp)
        }, verticalAlignment = Alignment.CenterVertically) {
            Text("业务信息", fontSize = 24.sp)
            IconButton({
                UserExtendFieldSettingScreenUiEvent.AddFieldConfig.sendTo(fieldConfigScreenModel)
            }) {
                Icon(painter = painterResource(Res.drawable.home), contentDescription = "")
            }
        }
        HorizontalDivider()
        val scrollState = rememberScrollState()

        Card(modifier = Modifier.padding {
            paddingVertical(5.dp)
        }) {
            Column(
                modifier = Modifier.padding(5.dp).heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
                    uFields.forEachIndexed { index, uField ->
                        Row {

                            val bfe = uFiledErrors[index]

                            var item by remember(uField) { mutableStateOf(uField) }
                            var error by remember(bfe) { mutableStateOf(bfe) }

                            InputView(
                                value = item.fieldName,
                                onValueChange = {
                                    item = item.copy(fieldName = it)
                                    UserExtendFieldSettingScreenUiEvent.UpdateBusinessFiled(index, item)
                                        .sendTo(fieldConfigScreenModel)
                                },
                                label = "字段名",
                                errorText = error.fieldName,
                                readOnly = false,
                                modifier = Modifier.width(150.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                            InputView(
                                value = item.description,
                                onValueChange = {
                                    item = item.copy(description = it)
                                    UserExtendFieldSettingScreenUiEvent.UpdateBusinessFiled(index, item)
                                        .sendTo(fieldConfigScreenModel)

                                },
                                label = "描述",
                                errorText = error.description,
                                modifier = Modifier.width(150.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                            InputView(
                                value = item.validationRule,
                                onValueChange = {
                                    item = item.copy(validationRule = it)
                                    UserExtendFieldSettingScreenUiEvent.UpdateBusinessFiled(index, item)
                                        .sendTo(fieldConfigScreenModel)

                                },
                                label = "校验规则",
                                errorText = error.validationRule,
                                modifier = Modifier.width(160.dp)
                                    .padding { paddingHorizontal(2.dp) }
                            )
                        }
                    }


                }

                HorizontalScrollbar(scrollState)
            }
        }

        Button({
            UserExtendFieldSettingScreenUiEvent.SaveFiledConfig.sendTo(fieldConfigScreenModel)
        }) {
            Text("保存")
        }
    }
}