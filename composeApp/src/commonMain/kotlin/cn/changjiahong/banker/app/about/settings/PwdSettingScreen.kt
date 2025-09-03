package cn.changjiahong.banker.app.about.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.ScaffoldWithTopBar
import cn.changjiahong.banker.utils.padding

object PwdSettingScreen : Screen {
    @Composable
    override fun Content() {

        val pwdSettingViewModel = koinScreenModel<PwdSettingViewModel>()
        val globalNavigator = GlobalNavigator.current

        pwdSettingViewModel.handleEffect {
            when {
                it is PwdSettingEffect.SaveSuccess -> {
                    globalNavigator.pop()
                    true
                }

                else -> false
            }
        }

        ScaffoldWithTopBar("密码修改") { pd ->


            var oldPwd by pwdSettingViewModel.oldPwd
            var newPwd by pwdSettingViewModel.newPwd
            var newPwd2 by pwdSettingViewModel.newPwd2

            Column(
                modifier = Modifier.fillMaxSize().padding(pd),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    label = { Text("原密码") },
                    value = oldPwd,
                    onValueChange = {
                        oldPwd = it
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation('*'),
                    modifier = Modifier.padding(5.dp)
                )
                OutlinedTextField(
                    label = { Text("新密码") },
                    value = newPwd,
                    onValueChange = {
                        newPwd = it
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation('*'),
                    modifier = Modifier.padding(5.dp)
                )

                OutlinedTextField(
                    label = { Text("再次输入新密码") },
                    value = newPwd2,
                    onValueChange = {
                        newPwd2 = it
                    },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation('*'),
                    modifier = Modifier.padding(5.dp)
                )

                val errorText by pwdSettingViewModel.error.collectAsState()
                if (errorText.isNotBlank()) {
                    Text(errorText, color = Color.Red, fontSize = 20.sp)
                }

                Button({
                    PwdSettingEvent.Submit.sendTo(pwdSettingViewModel)
                }) {
                    Text("确定")
                }

            }
        }
    }
}