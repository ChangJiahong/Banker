package cn.changjiahong.banker.app.about.settings.business.tmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.sync
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.composable.HoverDeleteBox
import cn.changjiahong.banker.composable.TextFieldDropdown
import cn.changjiahong.banker.model.fieldTypes
import cn.changjiahong.banker.model.isTableType
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import org.koin.core.parameter.parametersOf

class BusinessFieldConfigScreen(val business: Business) : Screen {
    @Composable
    override fun Content() {
        val fieldConfigScreenModel =
            koinScreenModel<BusinessFieldConfigScreenModel> { parametersOf(business) }

        ScaffoldWithTopBar(
            "业务属性",
            iconPainter = painterResource(Res.drawable.sync),
            iconOnClick = {
                BFieldConfigScreenUiEvent.SyncConfigs
                    .sendTo(fieldConfigScreenModel)
            }) { pd ->
            FieldConfigView(Modifier.padding(pd), fieldConfigScreenModel)
        }
    }
}


@Composable
fun BusinessFieldConfigScreen.FieldConfigView(
    modifier: Modifier,
    fieldConfigScreenModel: BusinessFieldConfigScreenModel
) {
    val global = GlobalNavigator.current
    fieldConfigScreenModel.handleEffect {
        when {
            it is ConfigUiEffect.SaveSuccess -> {
                global.pop()
                true
            }

            else -> false
        }
    }
    Column(modifier.padding { paddingHorizontal(30.dp) }) {
        val businessFields by fieldConfigScreenModel.businessFiledConfigs.collectAsState()
        val businessFiledErrors by fieldConfigScreenModel.businessFiledErrors.collectAsState()

        Button({
            BFieldConfigScreenUiEvent.SaveFiledConfig.sendTo(fieldConfigScreenModel)
        }) {
            Text("保存")
        }
        HorizontalDivider()
        val scrollState = rememberScrollState()

        Card(modifier = Modifier.padding {
            paddingVertical(5.dp)
        }) {
            Column(
                modifier = Modifier.padding(5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                var ind = remember { 1 }
                businessFields.forEachIndexed { index, bField ->
//                    if (bField.isDelete) {
//                        return@forEachIndexed
//                    }
                    Row(
                        Modifier.wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val bfe = businessFiledErrors[index]

                        var item by remember(bField) { mutableStateOf(bField) }
                        var error by remember(bfe) { mutableStateOf(bfe) }

                        val englishRegex = Regex("^$|^(?=.*[a-zA-Z])[a-zA-Z0-9]*$")

                        HoverDeleteBox((ind++).toString(), Modifier.padding {
                            paddingHorizontal(3.dp)
                            paddingTop(5.dp)
                        }, enable = false)

                        InputView(
                            value = item.label,
                            label = "字段名",
                            readOnly = true,
                            modifier = Modifier.width(150.dp)
                                .padding { paddingHorizontal(2.dp) }
                        )
                        InputView(
                            value = item.tag,
                            onValueChange = {
                                item = item.copy(tag = it)
                                BFieldConfigScreenUiEvent.UpdateBusinessFiled(
                                    index,
                                    item
                                )
                                    .sendTo(fieldConfigScreenModel)
                            },
                            label = "分类标签",
                            errorText = error.validationRule,
                            modifier = Modifier.width(160.dp)
                                .padding { paddingHorizontal(2.dp) }
                        )

                    }


                }
            }
        }
    }
}