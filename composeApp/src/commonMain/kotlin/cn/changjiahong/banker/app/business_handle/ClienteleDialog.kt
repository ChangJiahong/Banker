package cn.changjiahong.banker.app.business_handle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import cn.changjiahong.banker.FieldConfig
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.UIMeta
import cn.changjiahong.banker.composable.VisibleState
import cn.changjiahong.banker.composable.PopupDialog
import cn.changjiahong.banker.model.FieldVal
import cn.changjiahong.banker.model.MetaVal
import cn.changjiahong.banker.model.Table
import cn.changjiahong.banker.model.UIMetaField
import cn.changjiahong.banker.model.isTableType
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.getValue

/**
 *
 * @author ChangJiahong
 * @date 2025/9/1
 */


@Composable
fun ClienteleDialog(
    businessHandlerScreenModel: BusinessHandlerScreenModel,
    visibleState: VisibleState
) {
    PopupDialog(
        title = "新增信息",
        popupVisibleState = visibleState,
        modifier = Modifier.width(850.dp).fillMaxHeight().padding(30.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            val uiState by businessHandlerScreenModel.uiState.collectAsState()

            val fieldValues by businessHandlerScreenModel.fieldValues.collectAsState()
            val metaValues by businessHandlerScreenModel.metaValues.collectAsState()
            val fieldErrorMsg = businessHandlerScreenModel.fieldErrorMsg

            val uiMetas by businessHandlerScreenModel.uiMetas.collectAsState()

            val uiGroup = uiMetas.groupBy { metaField -> metaField.tag }

            uiGroup.forEach { (tag, metaFields) ->

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(tag, modifier = Modifier.padding(10.dp, 0.dp), fontSize = 24.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(10.dp, 0.dp))
                FlowRow(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {

                    metaFields.forEachIndexed { index, uiMeta ->

                        var fieldVal by remember {
                            mutableStateOf(
                                metaValues[uiMeta.metaId] ?: MetaVal(
                                    uiMeta.metaId,
                                )
                            )
                        }

                        when {

                            uiMeta.metaType == "TEXT" -> {
                                InputView(
                                    label = uiMeta.label,
                                    value = fieldVal.metaValue,
                                    modifier = Modifier.width((uiMeta.width.toInt() * 10).dp)
                                        .padding(10.dp, 0.dp),
                                    onValueChange = { newValue ->
                                        fieldVal = fieldVal.copy(metaValue = newValue)
                                        BhUIEvent.UpdateFieldValue(
                                            uiMeta.metaId,
                                            fieldVal
                                        ).sendTo(businessHandlerScreenModel)

                                    },
                                    errorText = fieldErrorMsg[uiMeta.metaId] ?: "",
                                )
                            }

                            uiMeta.metaType.isTableType() -> {
                                TableHander(uiMeta, businessHandlerScreenModel)
                            }

                        }

                    }


                }

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


@Composable
private fun TableHander(
    field: UIMetaField,
    businessHandlerScreenModel: BusinessHandlerScreenModel
) {
    val optionsFields by businessHandlerScreenModel.optionsFields.collectAsState()
    val optionsKey by businessHandlerScreenModel.optionsKey.collectAsState()

    if (!optionsKey.containsKey(field.metaId)) {
        return
    }
    Spacer(modifier = Modifier.fillMaxWidth())
    Card(
        Modifier.padding {
            paddingHorizontal(10.dp)
            paddingVertical(5.dp)
        },
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(Modifier.padding(5.dp)) {
            val options = optionsKey[field.metaId]!!
            var table by remember(optionsFields) {
                mutableStateOf(
                    optionsFields[field.metaId] ?: Table(
                        options,
                        field.metaId
                    )
                )
            }
            Row {
                Box(
                    Modifier.height(30.dp)
                        .padding { paddingHorizontal(10.dp) }, contentAlignment = Alignment.Center
                ) {
                    Text(field.label)
                }
                IconButton({
                    BhUIEvent.UpdateOptionV(field.metaId, table.copy { createRow() })
                        .sendTo(businessHandlerScreenModel)
                }, Modifier.height(30.dp)) {
                    Icon(Icons.Default.Add, "")
                }
            }
            /*
            表头
             */
            Row(
                Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(30.dp)
                            .border(1.dp, Color.Gray),
                        contentAlignment = Alignment.Center // 水平 + 垂直居中
                    ) {
                        Text(it)
                    }
                }
            }


            var rIndex = 0
            for (row in table.table()) {

                Row(
                    Modifier.height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    var cIndex = 0
                    for ((key, value) in row) {
                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .height(30.dp)
                                .border(1.dp, Color.Gray),
                            contentAlignment = Alignment.Center // 水平 + 垂直居中
                        ) {
                            var va by remember(value) { mutableStateOf(value) }

                            BasicTextField(
                                value = va,
                                modifier = Modifier.fillMaxWidth()
                                    .padding(2.dp, 0.dp),
                                textStyle = TextStyle.Default.copy(
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center // 水平居中
                                ),
                                singleLine = true,
                                onValueChange = { newValue ->
                                    va = newValue
                                    BhUIEvent.UpdateOptionV(
                                        field.metaId,
                                        table.copy {
                                            updateRow(row.copy(key, newValue))
                                        }).sendTo(businessHandlerScreenModel)
                                }
                            )
                        }

                        cIndex++
                    }
                }

                rIndex++
            }

        }
    }
}