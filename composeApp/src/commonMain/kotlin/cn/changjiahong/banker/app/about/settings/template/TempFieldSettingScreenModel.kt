package cn.changjiahong.banker.app.about.settings.template

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.model.TempField
import cn.changjiahong.banker.model.TempFieldError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.TemplateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface TFSUiEvent : UiEvent {
    object AddNewFieldConfig : TFSUiEvent

    object SaveConfig : TFSUiEvent

    class UpdateTempFieldConfig(val index: Int, val tempField: TempField) : TFSUiEvent

}

sealed interface TFSUiEffect : UiEffect {
    object SaveSuccess : TFSUiEffect
}

@Factory
class TempFieldSettingScreenModel(val template: DocTemplate, val templateService: TemplateService) :
    MviScreenModel() {

    private val _tempFieldConfigs = MutableStateFlow<List<TempField>>(emptyList())
    private val _tempFieldConfigsError = MutableStateFlow<List<TempFieldError>>(emptyList())

    val tempFieldConfigs = _tempFieldConfigs.asStateFlow()
    val tempFieldConfigsError = _tempFieldConfigsError.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            is TFSUiEvent.AddNewFieldConfig -> {
                var hasE = false
                if (_tempFieldConfigs.value.isNotEmpty()) {
                    val lastItem = _tempFieldConfigs.value.last()
                    var fE = ""
                    var tE = ""
                    if (lastItem.fieldName == null || lastItem.fieldName.isEmpty()) {
                        fE = "不能为空"
                        hasE = true
                    }
                    if (lastItem.fieldType == null || lastItem.fieldType.isEmpty()) {
                        tE = "不能为空"
                        hasE = true
                    }
                    _tempFieldConfigsError.replace(_tempFieldConfigsError.value.lastIndex) {
                        TempFieldError(
                            fE,
                            tE
                        )
                    }
                }
                if (hasE) return

                _tempFieldConfigs.update { it + TempField() }
                _tempFieldConfigsError.update { it + TempFieldError() }
            }

            is TFSUiEvent.UpdateTempFieldConfig -> {
                _tempFieldConfigs.replace(event.index) { event.tempField }
            }

            is TFSUiEvent.SaveConfig -> saveConfig()
        }
    }

    private fun saveConfig() {

        val values = _tempFieldConfigs.value
        var hasE = false
        val error = mutableListOf<TempFieldError>()
        values.forEachIndexed { index, field ->
            var fE = ""
            var tE = ""
            if (field.fieldName == null || field.fieldName.isEmpty()) {
                fE = "不能为空"
                hasE = true
            }
            if (field.fieldType == null || field.fieldType.isEmpty()) {
                tE = "不能为空"
                hasE = true
            }
            error += TempFieldError(fE, tE)
        }

        _tempFieldConfigsError.value = error

        if (hasE) return


        screenModelScope.launch {
            templateService.saveOrUpdateFieldsConfig(template.id, values).collect {
                TFSUiEffect.SaveSuccess.trigger()
            }
        }
    }

    init {
        loadTempFieldConfigs()
    }

    private fun loadTempFieldConfigs() {
        screenModelScope.launch {
            templateService.getFieldsByTemplateId(template.id).collect { data ->
                _tempFieldConfigs.value =
                    data.map { TempField(it.id, it.formFieldName, it.formFieldType) }

                _tempFieldConfigsError.value = MutableList(data.size) { TempFieldError() }
            }
        }
    }
}