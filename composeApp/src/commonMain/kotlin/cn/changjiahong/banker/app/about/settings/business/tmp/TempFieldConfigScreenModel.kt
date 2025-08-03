package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.composable.Option
import cn.changjiahong.banker.model.TBField
import cn.changjiahong.banker.model.TBFieldError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.TemplateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface FieldConfigScreenUiEvent : UiEvent {
    object AddFieldConfig : FieldConfigScreenUiEvent
    class UpdateBusinessFiled(val index: Int, val field: TBField) : FieldConfigScreenUiEvent

    object SaveConfig : UiEvent
}

sealed interface FieldConfigScreenUiEffect : UiEffect {
    object SaveSuccess : FieldConfigScreenUiEffect
}

@Factory
class FieldConfigScreenModel(
    val business: Business, val template: DocTemplate,
    val templateService: TemplateService,
    val businessService: BusinessService
) : MviScreenModel() {

    private val _templateOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val templateOptions = _templateOptions.asStateFlow()

    private val _businessOptions = MutableStateFlow<List<Option<Long>>>(emptyList())

    val businessOptions = _businessOptions.asStateFlow()

    private val _btFieldConfigs = MutableStateFlow<List<TBField>>(emptyList())
    private val _btFieldConfigsError = MutableStateFlow<List<TBFieldError>>(emptyList())

    val btFieldConfigs = _btFieldConfigs.asStateFlow()
    val btFieldConfigsError = _btFieldConfigsError.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            is FieldConfigScreenUiEvent.AddFieldConfig -> {
                _btFieldConfigs.update { it + TBField() }
                _btFieldConfigsError.update { it + TBFieldError() }
            }

            is FieldConfigScreenUiEvent.UpdateBusinessFiled ->
                _btFieldConfigs.replace(event.index) { event.field }

            is FieldConfigScreenUiEvent.SaveConfig -> saveConfig()
        }
    }

    private fun saveConfig() {
        println(btFieldConfigs.value)
        val btValue = btFieldConfigs.value
        val error = mutableListOf<TBFieldError>()
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
            error.add(TBFieldError(tf, bf, fv))
        }
        _btFieldConfigsError.value = error
        if (hasError) {
            return
        }

        screenModelScope.launch {
            businessService.saveBusinessTemplateFieldConfig(_btFieldConfigs.value).collect {
                FieldConfigScreenUiEffect.SaveSuccess.trigger()
            }
        }
    }

    init {
        loadTemplateFields()
        loadBusinessFields()
        loadFieldMap()
    }

    private fun loadFieldMap() {
        screenModelScope.launch {
            businessService.getFieldConfigMapByBidAndTid(business.id, template.id).collect { data ->
                _btFieldConfigs.value = data.map {
                    TBField(
                        it.id, it.tFieldId, it.bFieldId, it.isFixed > 0,
                        it.fixedValue
                    )
                }
                _btFieldConfigsError.value = MutableList(data.size){ TBFieldError() }

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