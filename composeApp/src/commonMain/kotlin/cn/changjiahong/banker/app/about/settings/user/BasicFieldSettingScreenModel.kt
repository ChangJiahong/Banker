package cn.changjiahong.banker.app.about.settings.user

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.model.BasicFieldConfig
import cn.changjiahong.banker.model.BasicFieldConfigError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface UserExtendFieldSettingScreenUiEvent : UiEvent {
    object AddFieldConfig : UiEvent
    class UpdateBusinessFiled(val index: Int, val uField: BasicFieldConfig) : UiEvent
    object SaveFiledConfig : UiEvent
}

@Factory
class UserExtendFieldSettingScreenModel(val userService: UserService) : MviScreenModel() {

    private val _uFiledConfigs = MutableStateFlow<List<BasicFieldConfig>>(emptyList())

    private val _uFiledErrors = MutableStateFlow<List<BasicFieldConfigError>>(emptyList())

    val uFiledConfigs = _uFiledConfigs.asStateFlow()
    val uFiledErrors = _uFiledErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is UserExtendFieldSettingScreenUiEvent.AddFieldConfig -> {
                _uFiledConfigs.update {
                    it + BasicFieldConfig()
                }
                _uFiledErrors.update {
                    it + BasicFieldConfigError()
                }
            }

            is UserExtendFieldSettingScreenUiEvent.SaveFiledConfig -> saveConfig()

            is UserExtendFieldSettingScreenUiEvent.UpdateBusinessFiled -> _uFiledConfigs.replace(
                event.index
            ) { event.uField }
        }
    }

    private fun saveConfig() {
        screenModelScope.launch {
            val bf = uFiledConfigs.value
            val be = mutableListOf<BasicFieldConfigError>()
            bf.forEachIndexed { index, field ->
                var fE = ""
                var dE = ""
                if (field.fieldName.isEmpty()) {
                    fE = "字段名称不能为空"
                }
                if (field.description.isEmpty()) {
                    dE = "描述不能为空"
                }
                val error = BasicFieldConfigError(fE, dE, "")
                be.add(error)
            }

            if (be.any { b -> b.fieldName.isNotEmpty() || b.description.isNotEmpty() || b.validationRule.isNotEmpty() }) {
                _uFiledErrors.value = be
                return@launch
            }

            userService.saveUFieldConfigs(_uFiledConfigs.value).collect {
                println("SU OK OK")
            }

        }
    }

    init {
        loadFiledConfigs()
    }

    private fun loadFiledConfigs() {
        screenModelScope.launch {
            userService.getUserExtendFields().collect {
                _uFiledErrors.value =
                    MutableList(it.size) {
                        BasicFieldConfigError()
                    }

                _uFiledConfigs.value = it.map { field ->
                    field.run {
                        BasicFieldConfig(
                            id, fieldName, "", description, validationRule, forced == 1L
                        )
                    }
                }
            }
        }
    }
}