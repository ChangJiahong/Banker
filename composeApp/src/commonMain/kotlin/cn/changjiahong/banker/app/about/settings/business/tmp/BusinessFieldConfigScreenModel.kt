package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.TemplateField
import cn.changjiahong.banker.model.BField
import cn.changjiahong.banker.model.BFieldError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface BFieldConfigScreenUiEvent : UiEvent {
    object AddFieldConfig : UiEvent
    class UpdateBusinessFiled(val index: Int, val bField: BField) : UiEvent
    object SaveFiledConfig : UiEvent
}

@Factory
class BusinessFieldConfigScreenModel(val business: Business, val businessService: BusinessService) :
    MviScreenModel() {

    private val _businessFiledConfigs = MutableStateFlow<List<BField>>(emptyList())

    private val _businessFiledErrors = MutableStateFlow<List<BFieldError>>(emptyList())

    val businessFiledConfigs = _businessFiledConfigs.asStateFlow()
    val businessFiledErrors = _businessFiledErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is BFieldConfigScreenUiEvent.AddFieldConfig -> {
                _businessFiledConfigs.update {
                    it + BField(businessId = business.id)
                }
                _businessFiledErrors.update {
                    it + BFieldError()
                }
            }

            is BFieldConfigScreenUiEvent.SaveFiledConfig -> saveConfig()

            is BFieldConfigScreenUiEvent.UpdateBusinessFiled -> _businessFiledConfigs.replace(event.index) { event.bField }
        }
    }

    private fun saveConfig() {
        screenModelScope.launch {
            val bf = businessFiledConfigs.value
            val be = mutableListOf<BFieldError>()
            bf.forEachIndexed { index, field ->
                var fE = ""
                var dE = ""
                if (field.fieldName.isEmpty()) {
                    fE = "字段名称不能为空"
                }
                if (field.description.isEmpty()) {
                    dE = "描述不能为空"
                }
                val error = BFieldError(fE, dE, "")
                be.add(error)
            }

            if (be.any { b -> b.fieldName.isNotEmpty() || b.description.isNotEmpty() || b.validationRule.isNotEmpty() }) {
                _businessFiledErrors.value = be
                return@launch
            }

            businessService.saveBFieldConfigs(_businessFiledConfigs.value).collect {
                println("SU OK OK")
            }

        }
    }

    init {
        loadFiledConfigs()
    }

    private fun loadFiledConfigs() {
        screenModelScope.launch {
            businessService.getFieldsById(business.id).collect {
                _businessFiledErrors.value =
                    MutableList(it.size) {
                        BFieldError()
                    }

                _businessFiledConfigs.value = it.map { field ->
                    field.run {
                        BField(
                            id, businessId, fieldName,
                            "",
                            fieldType,
                            description, validationRule ?: "", isFixed > 0, fixedValue ?: ""
                        )
                    }
                }
            }

        }
    }
}