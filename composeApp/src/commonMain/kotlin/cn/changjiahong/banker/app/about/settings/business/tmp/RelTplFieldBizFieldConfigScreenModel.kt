package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.composable.Option
import cn.changjiahong.banker.model.FieldOverrideBindingConfig
import cn.changjiahong.banker.model.RelTplFieldBasicFieldConfig
import cn.changjiahong.banker.model.UIMetaConfig
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UIMetaService
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface FieldConfigScreenUiEvent : UiEvent {
    object AddFieldConfig : FieldConfigScreenUiEvent
    class UpdateFiledConfig(val index: Int, val field: FieldOverrideBindingConfig) :
        FieldConfigScreenUiEvent

    object SaveConfig : UiEvent
}

sealed interface FieldConfigScreenUiEffect : UiEffect {
    object SaveSuccess : FieldConfigScreenUiEffect
}

@Factory
class FieldConfigScreenModel(
    val business: Business, val template: Template,
    val templateService: TemplateService,
    val businessService: BusinessService,
    val uiMetaService: UIMetaService
) : MviScreenModel() {

    private val _tplFieldOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val tplFieldOptions = _tplFieldOptions.asStateFlow()


    private val _uiMetaConfigs = MutableStateFlow<List<UIMetaConfig>>(emptyList())

    val uiMetaConfigs = _uiMetaConfigs.asStateFlow()

    private val _fieldConfigs = MutableStateFlow<List<FieldOverrideBindingConfig>>(emptyList())
    private val _fieldConfigsError =
        MutableStateFlow<List<FieldOverrideBindingConfig.Error>>(emptyList())

    val fieldConfigs = _fieldConfigs.asStateFlow()
    val fieldConfigsError = _fieldConfigsError.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            is FieldConfigScreenUiEvent.AddFieldConfig -> {
                _fieldConfigs.update {
                    it + FieldOverrideBindingConfig(
                        bId = business.id,
                        tId = template.id,
                    )
                }
                _fieldConfigsError.update { it + FieldOverrideBindingConfig.Error() }
            }

            is ConfigUiEvent.Delete -> {
                val field = _fieldConfigs.value[event.index]
                if (field.id < 0) {
                    _fieldConfigs.update { it.toMutableList().apply { removeAt(event.index) } }
                } else {
                    _fieldConfigs.replace(
                        event.index
                    ) { field.copy(isDelete = true) }
                }
            }

            is FieldConfigScreenUiEvent.UpdateFiledConfig ->
                _fieldConfigs.replace(event.index) { event.field }

            is FieldConfigScreenUiEvent.SaveConfig -> saveConfig()
        }
    }

    private fun saveConfig() {
        val btValue = fieldConfigs.value
        val error = mutableListOf<FieldOverrideBindingConfig.Error>()
        var hasError = false
        btValue.forEach { b ->
            var tf = ""
            var bf = ""
            var fv = ""
            if (b.fieldId < 0) {
                tf = "不能为空"
                hasError = true
            }
            error.add(FieldOverrideBindingConfig.Error(tf, bf, fv))
        }
        _fieldConfigsError.value = error
        if (hasError) {
            return
        }

        screenModelScope.launch {

            businessService.saveFieldOverrideBindingConfig(_fieldConfigs.value).catchAndCollect {
                FieldConfigScreenUiEffect.SaveSuccess.trigger()
            }

        }
    }

    init {
        loadTemplateFields()
        loadUIMetaConfigs()
        loadFieldMap()
    }

    private fun loadFieldMap() {
        screenModelScope.launch {

            businessService.getFieldOverrideBindingConfigs(business.id, template.id)
                .catchAndCollect { data ->
                    _fieldConfigs.value = data
                    _fieldConfigsError.value =
                        MutableList(data.size) { FieldOverrideBindingConfig.Error() }
                }

        }
    }

    private fun loadUIMetaConfigs() {
        screenModelScope.launch {
            uiMetaService.getUIMetaConfigs().catchAndCollect {
                _uiMetaConfigs.value = it
            }
        }
    }


    private fun loadTemplateFields() {
        screenModelScope.launch {
            templateService.getFieldConfigsByTid(template.id).collect { data ->
                _tplFieldOptions.value = data.map { Option(it.formFieldName, it.fieldId) }
            }
        }
    }

}