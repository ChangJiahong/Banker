package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.model.FieldConf
import cn.changjiahong.banker.model.FieldConfError
import cn.changjiahong.banker.model.RelBizUIMetaConfig
import cn.changjiahong.banker.model.RelBizUIMetaItem
import cn.changjiahong.banker.model.RelBizUIMetaTag
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
    class UpdateMetaTag(val index: Int, val tag: RelBizUIMetaTag) : UiEvent

    class SwapField(val fromIndex: Int, val toIndex: Int) : BFieldConfigScreenUiEvent
    object SaveFiledConfig : UiEvent

    class NewTag(val tagName: String) : BFieldConfigScreenUiEvent
}

@Factory
class BusinessFieldConfigScreenModel(
    val business: Business,
    val businessService: BusinessService,
    val fieldService: FieldService
) :
    MviScreenModel() {

    private val _relBizUIMetaConfigs = MutableStateFlow<List<RelBizUIMetaItem>>(emptyList())

    private val _businessFiledErrors = MutableStateFlow<List<FieldConfError>>(emptyList())

    val businessFiledConfigs = _relBizUIMetaConfigs.asStateFlow()
    val businessFiledErrors = _businessFiledErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {

            is BFieldConfigScreenUiEvent.SyncConfigs -> {
                syncConfigs()
            }

            is BFieldConfigScreenUiEvent.SwapField -> {
                _relBizUIMetaConfigs.update {
                    _relBizUIMetaConfigs.value.toMutableList()
                        .apply { add(event.toIndex, removeAt(event.fromIndex)) }
                }
            }


            is BFieldConfigScreenUiEvent.SaveFiledConfig -> saveConfig()

            is BFieldConfigScreenUiEvent.UpdateBusinessFiled -> {
                _relBizUIMetaConfigs.replace(event.index) { event.bField }
            }

            is BFieldConfigScreenUiEvent.UpdateMetaTag -> {
                _relBizUIMetaConfigs.replace(event.index) { event.tag }
            }
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


            /*
            更新排序
             */
            val items = _relBizUIMetaConfigs.value.toMutableList()
            var lastTag = ""
            var tagIndex = 0L
            items.forEachIndexed { index, config ->
                if (config is RelBizUIMetaConfig) {
                    items[index] = config.copy(
                        weight = index.toLong(),
                        tag = lastTag,
                        tagWeight = if (lastTag.isNotEmpty()) tagIndex else -1L
                    )
                } else if (config is RelBizUIMetaTag) {
                    lastTag = config.tagName
                    tagIndex++
                }
            }

            val configs = items.filter { it is RelBizUIMetaConfig }.map { it as RelBizUIMetaConfig }

            businessService.saveBizAndUIMetaRelConfigs(configs).catchAndCollect {
                ConfigUiEffect.SaveSuccess.trigger()
            }


        }
    }

    init {
        loadFiledConfigs()
    }

    private fun loadFiledConfigs() {
        screenModelScope.launch {

            businessService.getBizAndUIMetaRelList(business.id).catchAndCollect { it ->

                val tagGroup =
                    it.groupBy { config -> config.tag }.values.sortedBy { config -> config.first().tagWeight }

                val uiMetaItems = mutableListOf<RelBizUIMetaItem>()
                tagGroup.forEach { metaConfigs ->
                    uiMetaItems.add(RelBizUIMetaTag(metaConfigs.first().tag))
                    metaConfigs.forEach { m ->
                        uiMetaItems.add(m)
                    }
                }

                _relBizUIMetaConfigs.value = uiMetaItems

                _businessFiledErrors.value =
                    MutableList(uiMetaItems.size) {
                        FieldConfError()
                    }
            }


        }
    }
}