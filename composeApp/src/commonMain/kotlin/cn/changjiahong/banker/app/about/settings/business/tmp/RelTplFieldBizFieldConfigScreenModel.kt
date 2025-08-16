package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.composable.Option
import cn.changjiahong.banker.model.RelTplFieldBizFieldConfig
import cn.changjiahong.banker.model.RelTplFieldBizFieldConfigError
import cn.changjiahong.banker.model.RelTplFieldBasicFieldConfig
import cn.changjiahong.banker.model.RelTplFieldBasicFieldConfigError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface FieldConfigScreenUiEvent : UiEvent {
    object AddBFieldConfig : FieldConfigScreenUiEvent
    object AddUFieldConfig : FieldConfigScreenUiEvent
    class UpdateBusinessFiled(val index: Int, val field: RelTplFieldBizFieldConfig) :
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
    val userService: UserService,
) : MviScreenModel() {

    private val _templateOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val templateOptions = _templateOptions.asStateFlow()

    private val _businessOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val businessOptions = _businessOptions.asStateFlow()

    private val _userOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val userOptions = _userOptions.asStateFlow()

    private val _tuFieldConfigs = MutableStateFlow<List<RelTplFieldBasicFieldConfig>>(emptyList())
    private val _tuFieldConfigsError =
        MutableStateFlow<List<RelTplFieldBasicFieldConfigError>>(emptyList())

    private val _btFieldConfigs = MutableStateFlow<List<RelTplFieldBizFieldConfig>>(emptyList())
    private val _btFieldConfigsError =
        MutableStateFlow<List<RelTplFieldBizFieldConfigError>>(emptyList())

    val btFieldConfigs = _btFieldConfigs.asStateFlow()
    val btFieldConfigsError = _btFieldConfigsError.asStateFlow()

    val tuFieldConfigs = _tuFieldConfigs.asStateFlow()
    val tuFieldConfigsError = _tuFieldConfigsError.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            is FieldConfigScreenUiEvent.AddBFieldConfig -> {
                _btFieldConfigs.update { it + RelTplFieldBizFieldConfig() }
                _btFieldConfigsError.update { it + RelTplFieldBizFieldConfigError() }
            }

            is FieldConfigScreenUiEvent.AddUFieldConfig -> {
                _tuFieldConfigs.update { it + RelTplFieldBasicFieldConfig() }
                _tuFieldConfigsError.update { it + RelTplFieldBasicFieldConfigError() }
            }

            is FieldConfigScreenUiEvent.UpdateBusinessFiled ->
                _btFieldConfigs.replace(event.index) { event.field }

            is FieldConfigScreenUiEvent.UpdateUFiled ->
                _tuFieldConfigs.replace(event.index) { event.field }

            is FieldConfigScreenUiEvent.SaveConfig -> saveConfig()
        }
    }

    private fun saveConfig() {
        val btValue = btFieldConfigs.value
        val error = mutableListOf<RelTplFieldBizFieldConfigError>()
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
            error.add(RelTplFieldBizFieldConfigError(tf, bf, fv))
        }
        _btFieldConfigsError.value = error
        if (hasError) {
            return
        }

        screenModelScope.launch {

            userService.saveRelTplFieldBasicFieldConfig(business.id, _tuFieldConfigs.value)
                .catchAndCollect {
                    businessService.saveRelTplFieldBizFieldConfig(_btFieldConfigs.value)
                        .catchAndCollect {
                            FieldConfigScreenUiEffect.SaveSuccess.trigger()
                        }
                }
        }
    }

    init {
        loadTemplateFields()
        loadUserFields()
        loadBusinessFields()
        loadFieldMap()
    }

    private fun loadUserFields() {
        screenModelScope.launch {
            userService.getUserFields().collect { data ->
                _userOptions.value = data.map { Option(it.description, it.id) }
            }
        }
    }

    private fun loadFieldMap() {
        screenModelScope.launch {
            businessService.getFieldConfigMapByBidAndTid(business.id, template.id).collect { data ->
                _btFieldConfigs.value = data.map {
                    RelTplFieldBizFieldConfig(
                        it.id, it.tFieldId, it.bFieldId, it.isFixed > 0,
                        it.fixedValue
                    )
                }
                _btFieldConfigsError.value =
                    MutableList(data.size) { RelTplFieldBizFieldConfigError() }

            }

        }
        screenModelScope.launch {
            userService.getFieldConfigMapByTIdAndBId(template.id, business.id).collect { data ->
                _tuFieldConfigs.value = data.map {
                    RelTplFieldBasicFieldConfig(it.id, it.tFieldId, it.uFieldId)
                }
                _tuFieldConfigsError.value =
                    MutableList(data.size) { RelTplFieldBasicFieldConfigError() }
            }
        }
    }

    private fun loadBusinessFields() {
        screenModelScope.launch {
            businessService.getFieldsById(business.id).collect { data ->
                _businessOptions.value = data.map { Option(it.description, it.id) }
            }
        }
    }

    private fun loadTemplateFields() {
        screenModelScope.launch {
            templateService.getFieldsByTemplateId(template.id).collect { data ->
                _templateOptions.value = data.map { Option(it.formFieldName, it.id) }
            }
        }
    }

}