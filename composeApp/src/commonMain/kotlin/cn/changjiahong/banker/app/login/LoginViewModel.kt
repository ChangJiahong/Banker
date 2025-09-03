package cn.changjiahong.banker.app.login

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.SystemConfigService
import cn.changjiahong.banker.uieffect.GoEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface LoginEvent : UiEvent {
    object Login : LoginEvent
}

@Factory
class LoginViewModel (val systemConfigService: SystemConfigService): MviScreenModel() {
    private val _pwd = MutableStateFlow("")
    var pwd = mutableStateOf("")

    override fun handleEvent(event: UiEvent) {
        when(event){
            is LoginEvent.Login -> doLogin(pwd.value)
        }
    }

    private fun doLogin(pwd: String) {
        if (pwd.isEmpty()){
            toast("请输入密码")
            return
        }

        screenModelScope.launch {
            systemConfigService.getPwd().catchAndCollect {
                if (pwd==it){
                    GoEffect(RR.MAIN,true).trigger()
                }else{
                    toast("密码错误")
                }
            }
        }
    }
}