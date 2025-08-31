package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.composable.Option
import cn.changjiahong.banker.model.RelFieldConfigTplField
import cn.changjiahong.banker.model.RelFieldConfigTplFieldError
import cn.changjiahong.banker.model.RelTplFieldBasicFieldConfig
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface FieldConfigScreenUiEvent : UiEvent {
    object AddFieldConfig : FieldConfigScreenUiEvent
    object AddUFieldConfig : FieldConfigScreenUiEvent
    class UpdateFiledConfig(val index: Int, val field: RelFieldConfigTplField) :
        FieldConfigScreenUiEvent

    class UpdateUFiled(val index: Int, val field: RelTplFieldBasicFieldConfig) :
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
    val fieldService: FieldService,
    val userService: UserService,
) : MviScreenModel() {

    private val _tplFieldOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val tplFieldOptions = _tplFieldOptions.asStateFlow()

    private val _fieldOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val fieldOptions = _fieldOptions.asStateFlow()

    private val _fieldConfigs = MutableStateFlow<List<RelFieldConfigTplField>>(emptyList())
    private val _fieldConfigsError =
        MutableStateFlow<List<RelFieldConfigTplFieldError>>(emptyList())

    val fieldConfigs = _fieldConfigs.asStateFlow()
    val fieldConfigsError = _fieldConfigsError.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            is FieldConfigScreenUiEvent.AddFieldConfig -> {
                _fieldConfigs.update { it + RelFieldConfigTplField() }
                _fieldConfigsError.update { it + RelFieldConfigTplFieldError() }
            }

            is FieldConfigScreenUiEvent.UpdateFiledConfig ->
                _fieldConfigs.replace(event.index) { event.field }

            is FieldConfigScreenUiEvent.SaveConfig -> saveConfig()
        }
    }

    private fun saveConfig() {
        val btValue = fieldConfigs.value
        val error = mutableListOf<RelFieldConfigTplFieldError>()
        var hasError = false
        btValue.forEach { (id, tempFieldId, businessFieldId, isFixed, fixedValue) ->
            var tf = ""
            var bf = ""
            var fv = ""
            if (tempFieldId == null) {
                tf = "不能为空"
                hasError = true
            }
            if (!isFixed && businessFieldId == null) {
                bf = "不能为空"
                hasError = true
            }
            if (isFixed && fixedValue == null) {
                fv = "不能为空"
                hasError = true
            }
            error.add(RelFieldConfigTplFieldError(tf, bf, fv))
        }
        _fieldConfigsError.value = error
        if (hasError) {
            return
        }

        screenModelScope.launch {

            fieldService.saveFieldConfigAndTplFieldMap(business.id,_fieldConfigs.value).catchAndCollect {
                FieldConfigScreenUiEffect.SaveSuccess.trigger()
            }

        }
    }

    init {
        loadTemplateFields()
        loadFieldConfigs()
        loadFieldMap()
    }

    private fun loadFieldMap() {
        screenModelScope.launch {

            /*
            获取业务项（全局）属性和模版的属性映射关系
             */
            fieldService.getFieldConfigAndTplFieldMap(business.id, template.id)
                .catchAndCollect { data ->
                    _fieldConfigs.value = data.map {
                        RelFieldConfigTplField(
                            it.id, it.tFieldId, it.fieldId, it.isFixed > 0,
                            it.fixedValue
                        )
                    }
                    _fieldConfigsError.value =
                        MutableList(data.size) { RelFieldConfigTplFieldError() }
                }
        }
    }

    private fun loadFieldConfigs() {
        screenModelScope.launch {
            fieldService.getFieldConfigs(business.id).catchAndCollect { data ->
                _fieldOptions.value = data.map { Option(it.fieldName, it.fieldId) }
            }
        }
    }

    private fun loadTemplateFields() {
        screenModelScope.launch {
            templateService.getFieldsByTemplateId(template.id).collect { data ->
                _tplFieldOptions.value = data.map { Option(it.alias, it.id) }
            }
        }
    }

}