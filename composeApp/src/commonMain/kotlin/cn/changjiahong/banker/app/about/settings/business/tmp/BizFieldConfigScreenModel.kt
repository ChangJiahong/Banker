package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.model.FieldConf
import cn.changjiahong.banker.model.FieldConfError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.FieldService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface BFieldConfigScreenUiEvent : UiEvent {
    object AddFieldConfig : UiEvent
    class UpdateBusinessFiled(val index: Int, val bField: FieldConf) : UiEvent
    object SaveFiledConfig : UiEvent
}

@Factory
class BusinessFieldConfigScreenModel(
    val business: Business,
    val businessService: BusinessService,
    val fieldService: FieldService
) :
    MviScreenModel() {

    private val _businessFiledConfigs = MutableStateFlow<List<FieldConf>>(emptyList())

    private val _businessFiledErrors = MutableStateFlow<List<FieldConfError>>(emptyList())

    val businessFiledConfigs = _businessFiledConfigs.asStateFlow()
    val businessFiledErrors = _businessFiledErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is ConfigUiEvent.Add -> {
                _businessFiledConfigs.update {
                    it + FieldConf(bId = business.id)
                }
                _businessFiledErrors.update {
                    it + FieldConfError()
                }
            }

            is ConfigUiEvent.Delete -> {
                val field = _businessFiledConfigs.value[event.index]
                if (field.fieldId < 0) {
                    _businessFiledConfigs.update {
                        it.toMutableList().apply { removeAt(event.index) }
                    }
                } else {
                    _businessFiledConfigs.replace(
                        event.index
                    ) { field.copy(isDelete = true) }
                }
            }

            is BFieldConfigScreenUiEvent.SaveFiledConfig -> saveConfig()

            is BFieldConfigScreenUiEvent.UpdateBusinessFiled -> _businessFiledConfigs.replace(event.index) { event.bField }
        }
    }

    private fun saveConfig() {
        screenModelScope.launch {
            val bf = businessFiledConfigs.value
            val be = mutableListOf<FieldConfError>()
            bf.forEachIndexed { index, field ->
                var fE = ""
                var dE = ""
                if (field.fieldName.isEmpty()) {
                    fE = "字段名称不能为空"
                }
                if (field.alias.isEmpty()) {
                    dE = "描述不能为空"
                }
                val error = FieldConfError(fE, dE, "")
                be.add(error)
            }

            if (be.any { b -> b.fieldName.isNotEmpty() || b.alias.isNotEmpty() || b.validationRule.isNotEmpty() }) {
                _businessFiledErrors.value = be
                return@launch
            }

            fieldService.saveBizFieldConfigs(_businessFiledConfigs.value).catchAndCollect {
                ConfigUiEffect.SaveSuccess.trigger()
            }

        }
    }

    init {
        loadFiledConfigs()
    }

    private fun loadFiledConfigs() {
        screenModelScope.launch {

            fieldService.getBizFieldConfigsByBid(business.id).catchAndCollect {
                _businessFiledErrors.value =
                    MutableList(it.size) {
                        FieldConfError()
                    }

                _businessFiledConfigs.value = it.map { field ->
                    field.run {
                        FieldConf(
                            fieldId, bId, fieldName,
                            fieldType,
                            alias, width.toInt(), validationRule, field.forced > 0
                        )
                    }
                }
            }


        }
    }
}