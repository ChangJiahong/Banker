package cn.changjiahong.banker.app.about.settings.user

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.TemplateField
import cn.changjiahong.banker.UserExtendField
import cn.changjiahong.banker.model.BField
import cn.changjiahong.banker.model.BFieldError
import cn.changjiahong.banker.model.UExtendField
import cn.changjiahong.banker.model.UExtendFieldError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface UserExtendFieldSettingScreenUiEvent : UiEvent {
    object AddFieldConfig : UiEvent
    class UpdateBusinessFiled(val index: Int, val uField: UExtendField) : UiEvent
    object SaveFiledConfig : UiEvent
}

@Factory
class UserExtendFieldSettingScreenModel(val userService: UserService) : MviScreenModel() {

    private val _uFiledConfigs = MutableStateFlow<List<UExtendField>>(emptyList())

    private val _uFiledErrors = MutableStateFlow<List<UExtendFieldError>>(emptyList())

    val uFiledConfigs = _uFiledConfigs.asStateFlow()
    val uFiledErrors = _uFiledErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is UserExtendFieldSettingScreenUiEvent.AddFieldConfig -> {
                _uFiledConfigs.update {
                    it + UExtendField()
                }
                _uFiledErrors.update {
                    it + UExtendFieldError()
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
            val be = mutableListOf<UExtendFieldError>()
            bf.forEachIndexed { index, field ->
                var fE = ""
                var dE = ""
                if (field.fieldName.isEmpty()) {
                    fE = "字段名称不能为空"
                }
                if (field.description.isEmpty()) {
                    dE = "描述不能为空"
                }
                val error = UExtendFieldError(fE, dE, "")
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
                        UExtendFieldError()
                    }

                _uFiledConfigs.value = it.map { field ->
                    field.run {
                        UExtendField(
                            id, fieldName, description, validationRule ?: ""
                        )
                    }
                }
            }
        }
    }
}