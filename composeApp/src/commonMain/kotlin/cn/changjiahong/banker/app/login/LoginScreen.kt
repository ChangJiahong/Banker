package cn.changjiahong.banker.app.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.uieffect.ShowToast
import cn.changjiahong.banker.utils.padding


object LoginScreen : Screen {
    @Composable
    override fun Content() {

        val loginViewModel = koinScreenModel<LoginViewModel>()

        var pwd by loginViewModel.pwd

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding { paddingBottom(180.dp) }) {
                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
                OutlinedTextField(
                    label = {
                        Text(
                            "请输入密码",
                            fontSize = 28.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    modifier = Modifier.width(200.dp).focusRequester(focusRequester),
                    textStyle = TextStyle.Default.copy(
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center
                    ),
                    value = pwd,
                    onValueChange = { p ->
                        val filtered = p.filter { it.isLetterOrDigit() || it in "@._!#$%&*-" }
                        if (filtered.length <= 10) {
                            pwd = filtered
                        }
                    },
                    keyboardActions = KeyboardActions(onDone = {
                        LoginEvent.Login.sendTo(loginViewModel)
                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
//                    visualTransformation = PasswordVisualTransformation('*'),
                    singleLine = true,
                )

                OutlinedButton({
                    LoginEvent.Login.sendTo(loginViewModel)
                }, modifier = Modifier.padding {
                    paddingTop(10.dp)
                }) {
                    Text("确定", fontSize = 18.sp)
                }

            }
        }
    }
}