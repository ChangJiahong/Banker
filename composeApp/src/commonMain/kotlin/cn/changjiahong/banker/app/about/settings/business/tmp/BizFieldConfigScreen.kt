package cn.changjiahong.banker.app.about.settings.business.tmp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.sync
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.composable.DraggableItem
import cn.changjiahong.banker.composable.HoverDeleteBox
import cn.changjiahong.banker.composable.dragContainer
import cn.changjiahong.banker.composable.rememberDragDropState
import cn.changjiahong.banker.model.RelBizUIMetaConfig
import cn.changjiahong.banker.model.RelBizUIMetaTag
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

        Button({
            BFieldConfigScreenUiEvent.NewTag("").sendTo(fieldConfigScreenModel)
        }, Modifier.padding { paddingHorizontal(5.dp) }) {
            Text("新建标签")
        }

        HorizontalDivider()

        val scrollState = rememberScrollState()

        Card(
            modifier = Modifier.padding {
                paddingVertical(5.dp)
            },
            colors = CardDefaults.cardColors(containerColor = Color(0xffeaeefb))
        ) {

            val listState = rememberLazyListState()
            val dragDropState =
                rememberDragDropState(listState) { fromIndex, toIndex ->
//                    list = list.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
                    BFieldConfigScreenUiEvent.SwapField(fromIndex, toIndex)
                        .sendTo(fieldConfigScreenModel)
                }
            LazyColumn(
                Modifier.dragContainer(dragDropState).fillMaxWidth(),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                itemsIndexed(
                    businessFields, key = { _, item -> item.key },
                    contentType = { _, item ->
                        when (item) {
                            is RelBizUIMetaConfig -> 1
                            is RelBizUIMetaTag -> 2
                        }
                    }) { index, item ->
                    DraggableItem(dragDropState, index) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 8.dp else 1.dp)
                        Card(
                            modifier = Modifier.shadow(
                                elevation,
                                RoundedCornerShape(12.dp),
                                clip = false
                            ),
                            colors = CardDefaults.cardColors(containerColor = Color(0xfff2f3fc)),
//                        .clip(RoundedCornerShape(12.dp))
                        ) {
                            if (item is RelBizUIMetaConfig) {
                                val bField = item
                                Row(
                                    Modifier.wrapContentWidth().padding {
                                        paddingHorizontal(10.dp)
                                        paddingVertical(5.dp)
                                    },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val bfe = businessFiledErrors[index]

                                    var item by remember(bField) { mutableStateOf(bField) }
                                    var error by remember(bfe) { mutableStateOf(bfe) }

                                    val englishRegex = Regex("^$|^(?=.*[a-zA-Z])[a-zA-Z0-9]*$")

                                    HoverDeleteBox(index.toString(), Modifier.padding {
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

                                    Icon(
                                        Icons.Default.DragIndicator,
                                        contentDescription = "",
                                        Modifier.padding { paddingHorizontal(2.dp) })

                                }
                            } else if (item is RelBizUIMetaTag) {

//                                is RelBizUIMetaTag -> {

                                var ite by remember(item) { mutableStateOf(item) }

                                Box(
                                    Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val textFieValue =
                                        rememberSaveable() { TextFieldState(item.tagName.ifEmpty { "未分配标签" }) }
                                    var isFocused by remember { mutableStateOf(false) }

                                    BasicTextField(
                                        state = textFieValue,
                                        textStyle = TextStyle.Default.copy(
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Center
                                        ),
                                        modifier = Modifier.padding { paddingVertical(5.dp) }
                                            .height(24.dp).onFocusChanged {
                                                if (!it.isFocused && isFocused) {
                                                    BFieldConfigScreenUiEvent.UpdateMetaTag(
                                                        index,
                                                        item.copy(tagName = textFieValue.text.toString())
                                                    ).sendTo(fieldConfigScreenModel)
                                                }
                                                isFocused = it.isFocused
                                            },
                                    )

                                }
//                                }
                            }


                        }
                    }
                }
            }


//            Column(
//                modifier = Modifier.padding(5.dp)
//                    .verticalScroll(rememberScrollState())
//            ) {
//                var ind = remember { 1 }
//                businessFields.forEachIndexed { index, bField ->
////                    if (bField.isDelete) {
////                        return@forEachIndexed
////                    }
//
//
//
//                }
//            }
        }
    }
}