package cn.changjiahong.banker.app.business_handle

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import cn.changjiahong.banker.InputView
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.composable.PopupDialog
import cn.changjiahong.banker.model.FieldVal
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource

/**
 *
 * @author ChangJiahong
 * @date 2025/9/1
 */


@Composable
fun ClienteleDialog(
    businessHandlerScreenModel: BusinessHandlerScreenModel,
    dialogState: DialogState
) {
    PopupDialog(
        title = "新增信息",
        popupDialogState = dialogState,
        modifier = Modifier.width(850.dp).fillMaxHeight().padding(30.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            val uiState by businessHandlerScreenModel.uiState.collectAsState()

            val fieldValues by businessHandlerScreenModel.fieldValues.collectAsState()
            val optionsFields by businessHandlerScreenModel.optionsFields.collectAsState()
            val optionsKey by businessHandlerScreenModel.optionsKey.collectAsState()

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

                    when (field.fieldType) {

                        "TEXT" -> {
                            InputView(
                                label = field.fieldName,
                                value = fieldVal.fieldValue,
                                modifier = Modifier.width((field.width.toInt() * 10).dp)
                                    .padding(10.dp, 0.dp),
                                onValueChange = { newValue ->
                                    fieldVal = fieldVal.copy(fieldValue = newValue)
                                    BhUIEvent.UpdateFieldValue(
                                        field.fieldId,
                                        fieldVal
                                    ).sendTo(businessHandlerScreenModel)

                                },
//                        errorText = uiState.usernameError,
                            )
                        }

                        "ROW_TABLE" -> {
                            if (!optionsKey.containsKey(field.fieldId)) {
                                return@forEachIndexed
                            }
                            Spacer(modifier = Modifier.fillMaxWidth())
                            Column(Modifier.padding { paddingTop(5.dp) }) {
                                val options = optionsKey[field.fieldId]!!
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
                                    IconButton({
                                        BhUIEvent.AddOptionV(field.fieldId,"").sendTo(businessHandlerScreenModel)
                                    }, Modifier.height(30.dp)) {
                                        Icon(Icons.Default.Add, "")
                                    }
                                }
                                var optionV by remember(optionsFields) {
                                    mutableStateOf(
                                        optionsFields[field.fieldId]
                                    )
                                }

                                optionV?.forEachIndexed { index, map ->
                                    Row(
                                        Modifier.height(IntrinsicSize.Min),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        map.forEach { (key, value) ->
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
                                                    onValueChange = { newValue ->
                                                        va = newValue
                                                        BhUIEvent.UpdateOptionV(
                                                            field.fieldId,
                                                            index,
                                                            map.toMutableMap()
                                                                .apply { put(key, newValue) })
                                                            .sendTo(businessHandlerScreenModel)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                            }
                        }

                    }

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
                        label = field.fieldName,
                        modifier = Modifier.width((field.width.toInt() * 10).dp)
                            .padding(10.dp, 0.dp),
                        value = fieldVal.fieldValue,
                        onValueChange = {
                            fieldVal = fieldVal.copy(fieldValue = it)
                            BhUIEvent.UpdateFieldValue(
                                field.fieldId,
                                fieldVal
                            ).sendTo(businessHandlerScreenModel)

                        },
                        errorText = uiState.fieldErrorMsg[field.fieldName] ?: "",
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