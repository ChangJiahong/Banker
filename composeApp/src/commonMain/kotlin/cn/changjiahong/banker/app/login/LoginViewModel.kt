package cn.changjiahong.banker.app.login

import androidx.compose.runtime.mutableStateOf
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.annotation.Factory

sealed interface LoginEvent : UiEvent {
    object Login : LoginEvent
}

@Factory
class LoginViewModel : MviScreenModel() {
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

    }
}