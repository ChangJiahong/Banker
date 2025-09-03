package cn.changjiahong.banker.app.about.settings

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.SystemConfigService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface PwdSettingEvent : UiEvent {
    object Submit : PwdSettingEvent
}

sealed interface PwdSettingEffect : UiEffect {
    object SaveSuccess : PwdSettingEffect
}

@Factory
class PwdSettingViewModel(val systemConfigService: SystemConfigService) : MviScreenModel() {

    val oldPwd = mutableStateOf("")
    val newPwd = mutableStateOf("")
    val newPwd2 = mutableStateOf("")

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            PwdSettingEvent.Submit -> submit(oldPwd.value, newPwd.value, newPwd2.value)
        }
    }

    private fun submit(oldPwd: String, newPwd: String, newPwd2: String) {
        if (oldPwd.isBlank() || newPwd.isBlank() || newPwd2.isBlank()) {
            _error.update { "请输入密码" }
            return
        }
        if (newPwd != newPwd2) {
            _error.update { "两次输入的密码不一致" }
            return
        }
        if (newPwd2.length < 6) {
            _error.update { "密码长度不小于6" }
            return
        }
        screenModelScope.launch {
            systemConfigService.isFirstStart().catchAndCollect {
                if (it) {
                    if (oldPwd != "111111") {
                        _error.update { "原密码错误" }
                    } else {
                        systemConfigService.savePwd(newPwd).catchAndCollect {
                            systemConfigService.unFirstStart().catchAndCollect {
                                PwdSettingEffect.SaveSuccess.trigger()
                            }
                        }
                    }
                } else {
                    systemConfigService.getPwd().catchAndCollect {
                        if (oldPwd != it) {
                            _error.update { "原密码错误" }
                        } else {
                            systemConfigService.savePwd(newPwd).catchAndCollect {
                                PwdSettingEffect.SaveSuccess.trigger()
                            }
                        }
                    }
                }
            }

        }

    }
}