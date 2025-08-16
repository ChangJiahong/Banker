package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.BizFieldConfig
import cn.changjiahong.banker.model.BizFieldConfigError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface BFieldConfigScreenUiEvent : UiEvent {
    object AddFieldConfig : UiEvent
    class UpdateBusinessFiled(val index: Int, val bField: BizFieldConfig) : UiEvent
    object SaveFiledConfig : UiEvent
}

@Factory
class BusinessFieldConfigScreenModel(val business: Business, val businessService: BusinessService) :
    MviScreenModel() {

    private val _businessFiledConfigs = MutableStateFlow<List<BizFieldConfig>>(emptyList())

    private val _businessFiledErrors = MutableStateFlow<List<BizFieldConfigError>>(emptyList())

    val businessFiledConfigs = _businessFiledConfigs.asStateFlow()
    val businessFiledErrors = _businessFiledErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is BFieldConfigScreenUiEvent.AddFieldConfig -> {
                _businessFiledConfigs.update {
                    it + BizFieldConfig(bId = business.id)
                }
                _businessFiledErrors.update {
                    it + BizFieldConfigError()
                }
            }

            is BFieldConfigScreenUiEvent.SaveFiledConfig -> saveConfig()

            is BFieldConfigScreenUiEvent.UpdateBusinessFiled -> _businessFiledConfigs.replace(event.index) { event.bField }
        }
    }

    private fun saveConfig() {
        screenModelScope.launch {
            val bf = businessFiledConfigs.value
            val be = mutableListOf<BizFieldConfigError>()
            bf.forEachIndexed { index, field ->
                var fE = ""
                var dE = ""
                if (field.fieldName.isEmpty()) {
                    fE = "字段名称不能为空"
                }
                if (field.description.isEmpty()) {
                    dE = "描述不能为空"
                }
                val error = BizFieldConfigError(fE, dE, "")
                be.add(error)
            }

            if (be.any { b -> b.fieldName.isNotEmpty() || b.description.isNotEmpty() || b.validationRule.isNotEmpty() }) {
                _businessFiledErrors.value = be
                return@launch
            }

            businessService.saveOrUpdateBizFieldConfigs(_businessFiledConfigs.value).collect {
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
                        BizFieldConfigError()
                    }

                _businessFiledConfigs.value = it.map { field ->
                    field.run {
                        BizFieldConfig(
                            id, bId, fieldName,
                            fieldType,
                            description, validationRule, isFixed > 0, fixedValue
                        )
                    }
                }
            }

        }
    }
}