package cn.changjiahong.banker.app.about.settings.business.tmp

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
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.platform.HorizontalScrollbar
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

class BusinessFieldConfigScreen(val business: Business) : Screen {
    @Composable
    override fun Content() {
        ScaffoldWithTopBar("业务属性") { pd ->
            FieldConfigView(Modifier.padding(pd))
        }
    }
}


@Composable
fun BusinessFieldConfigScreen.FieldConfigView(modifier: Modifier) {
    val fieldConfigScreenModel = koinScreenModel<BusinessFieldConfigScreenModel> { parametersOf(business) }
    val global = GlobalNavigator.current
    fieldConfigScreenModel.handleEffect {
        when{
            it is ConfigUiEffect.SaveSuccess -> {
                global.pop()
                true
            }
            else -> false
        }
    }
    Column(modifier.padding { paddingHorizontal(10.dp) }) {
        val businessFields by fieldConfigScreenModel.businessFiledConfigs.collectAsState()
        val businessFiledErrors by fieldConfigScreenModel.businessFiledErrors.collectAsState()

        Row(Modifier.padding {
            paddingVertical(5.dp)
        }, verticalAlignment = Alignment.CenterVertically) {
            Text("业务信息", fontSize = 24.sp)
            IconButton({
                BFieldConfigScreenUiEvent.AddFieldConfig.sendTo(fieldConfigScreenModel)
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
                        businessFields.forEachIndexed { index, bField ->

                            Row {
                               val bfe = businessFiledErrors[index]

                                var item by remember(bField) { mutableStateOf(bField) }
                                var error by remember(bfe){mutableStateOf(bfe) }

                                val englishRegex = Regex("^$|^(?=.*[a-zA-Z])[a-zA-Z0-9]*$")


                                InputView(
                                    value = item.fieldName,
                                    onValueChange = {
                                        item = item.copy(fieldName = it)
                                        BFieldConfigScreenUiEvent.UpdateBusinessFiled(
                                            index,
                                            item
                                        )
                                            .sendTo(fieldConfigScreenModel)
                                    },
                                    label = "字段名",
                                    errorText = error.fieldName,
                                    readOnly = false,
                                    modifier = Modifier.width(150.dp)
                                        .padding { paddingHorizontal(2.dp) }
                                )
                                TextFieldDropdown(
                                    listOf("TEXT", "IMAGE"),
                                    item.fieldType,
                                    onValueChange = {
                                        item = item.copy(fieldType = it)
                                        BFieldConfigScreenUiEvent.UpdateBusinessFiled(
                                            index,
                                            item
                                        )
                                            .sendTo(fieldConfigScreenModel)
                                    },
                                    enableEdit = false,
                                    label = "字段类型",
                                    modifier = Modifier.width(140.dp)
                                        .padding { paddingHorizontal(2.dp) }
                                )
//                                InputView(
//                                    value = item.alias,
//                                    onValueChange = {
//                                        item = item.copy(alias = it)
//                                        BFieldConfigScreenUiEvent.UpdateBusinessFiled(
//                                            index,
//                                            item
//                                        )
//                                            .sendTo(fieldConfigScreenModel)
//                                    },
//                                    label = "描述",
//                                    errorText = error.alias,
//                                    modifier = Modifier.width(150.dp)
//                                        .padding { paddingHorizontal(2.dp) }
//                                )
                                InputView(
                                    value = item.validationRule,
                                    onValueChange = {
                                        item = item.copy(validationRule = it)
                                        BFieldConfigScreenUiEvent.UpdateBusinessFiled(
                                            index,
                                            item
                                        )
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
            BFieldConfigScreenUiEvent.SaveFiledConfig.sendTo(fieldConfigScreenModel)
        }) {
            Text("保存")
        }
    }
}