package cn.changjiahong.banker.app.login

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.app.about.settings.PwdSettingScreen
import cn.changjiahong.banker.composable.VisibleState
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.SystemConfigService
import cn.changjiahong.banker.uieffect.GoEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface LoginEvent : UiEvent {
    object Login : LoginEvent
}

sealed interface LoginEffect : UiEffect {
    object ToLoginView : LoginEffect
}

@Factory
class LoginViewModel(val systemConfigService: SystemConfigService) : MviScreenModel() {
    private val _pwd = MutableStateFlow("")
    var pwd = mutableStateOf("")

    val loadingState = VisibleState()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is LoginEvent.Login -> doLogin(pwd.value)
        }
    }

    private fun doLogin(pwd: String) {
        if (pwd.isEmpty()) {
            toast("请输入密码")
            return
        }

        screenModelScope.launch {
            loadingState.show()
            systemConfigService.isFirstStart().catchAndCollect({ loadingState.dismiss() }) {
                if (it) {
                    loadingState.dismiss()
                    if (pwd == "111111") {
                        GoEffect(RR.PWD_SETTING).trigger()
                    } else {
                        toast("密码错误")
                    }
                } else {
                    systemConfigService.getPwd().catchAndCollect {
                        loadingState.dismiss()
                        if (pwd == it) {
                            GoEffect(RR.MAIN, true).trigger()
                        } else {
                            toast("密码错误")
                        }
                    }
                }
            }
        }
    }
}