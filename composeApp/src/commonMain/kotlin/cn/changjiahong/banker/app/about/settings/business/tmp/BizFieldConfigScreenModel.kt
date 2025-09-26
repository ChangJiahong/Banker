package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.model.FieldConf
import cn.changjiahong.banker.model.FieldConfError
import cn.changjiahong.banker.model.RelBizUIMetaConfig
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

    object SyncConfigs : BFieldConfigScreenUiEvent
    class UpdateBusinessFiled(val index: Int, val bField: RelBizUIMetaConfig) : UiEvent
    object SaveFiledConfig : UiEvent
}

@Factory
class BusinessFieldConfigScreenModel(
    val business: Business,
    val businessService: BusinessService,
    val fieldService: FieldService
) :
    MviScreenModel() {

    private val _relBizUIMetaConfigs = MutableStateFlow<List<RelBizUIMetaConfig>>(emptyList())

    private val _businessFiledErrors = MutableStateFlow<List<FieldConfError>>(emptyList())

    val businessFiledConfigs = _relBizUIMetaConfigs.asStateFlow()
    val businessFiledErrors = _businessFiledErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {

            is BFieldConfigScreenUiEvent.SyncConfigs -> {
                syncConfigs()
            }

            is BFieldConfigScreenUiEvent.SaveFiledConfig -> saveConfig()

            is BFieldConfigScreenUiEvent.UpdateBusinessFiled -> _relBizUIMetaConfigs.replace(event.index) { event.bField }
        }
    }

    private fun syncConfigs() {
        screenModelScope.launch {
            businessService.autoGenerateBizAndUIMetaRelList(business.id).catchAndCollect {
                loadFiledConfigs()
            }
        }
    }

    private fun saveConfig() {
        screenModelScope.launch {
            val bf = businessFiledConfigs.value
            val be = mutableListOf<FieldConfError>()



            businessService.saveBizAndUIMetaRelConfigs(_relBizUIMetaConfigs.value).catchAndCollect {
                ConfigUiEffect.SaveSuccess.trigger()
            }


        }
    }

    init {
        loadFiledConfigs()
    }

    private fun loadFiledConfigs() {
        screenModelScope.launch {

            businessService.getBizAndUIMetaRelList(business.id).catchAndCollect {
                _relBizUIMetaConfigs.value = it
                _businessFiledErrors.value =
                    MutableList(it.size) {
                        FieldConfError()
                    }
            }


        }
    }
}