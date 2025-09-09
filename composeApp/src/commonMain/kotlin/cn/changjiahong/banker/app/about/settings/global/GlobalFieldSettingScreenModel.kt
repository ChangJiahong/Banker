package cn.changjiahong.banker.app.about.settings.global

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.model.FieldConf
import cn.changjiahong.banker.model.FieldConfError
import cn.changjiahong.banker.model.UIMetaConfig
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.UIMetaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface GlobalConfigUiEvent : UiEvent {

    class Update(val index: Int, val item: UIMetaConfig) : GlobalConfigUiEvent

}

@Factory
class GlobalFieldSettingScreenModel(
    val uiMetaService: UIMetaService
) : MviScreenModel() {

    private val _uiMetaConfigs = MutableStateFlow<List<UIMetaConfig>>(emptyList())

    private val _errors = MutableStateFlow<List<UIMetaConfig.Error>>(emptyList())

    val metaConfigs = _uiMetaConfigs.asStateFlow()
    val errors = _errors.asStateFlow()


    override fun handleEvent(event: UiEvent) {
        when (event) {
            is ConfigUiEvent.Add -> {
                _uiMetaConfigs.update {
                    it + UIMetaConfig()
                }
                _errors.update {
                    it + UIMetaConfig.Error()
                }
            }

            is ConfigUiEvent.Delete -> {
                val field = _uiMetaConfigs.value[event.index]
                if (field.metaId < 0) {
                    _uiMetaConfigs.update { it.toMutableList().apply { removeAt(event.index) } }
                } else {
                    _uiMetaConfigs.replace(
                        event.index
                    ) { field.copy(isDelete = true) }
                }
            }

            is ConfigUiEvent.Save -> saveConfig()

            is GlobalConfigUiEvent.Update -> _uiMetaConfigs.replace(
                event.index
            ) { event.item }
        }
    }

    private fun saveConfig() {
        screenModelScope.launch {
            val bf = _uiMetaConfigs.value
            val be = mutableListOf<UIMetaConfig.Error>()
            bf.forEachIndexed { index, meta ->
                if (meta.isDelete){
                    be.add(UIMetaConfig.Error())
                    return@forEachIndexed
                }
                var fE = ""
                if (meta.label.isEmpty()) {
                    fE = "字段名称不能为空"
                }
                var wE = ""
                if (meta.width <10){
                    wE = "长度不允许小于10"
                }
                val error = UIMetaConfig.Error(fE, width = wE)
                be.add(error)
            }

            if (be.any { b -> b.label.isNotEmpty() || b.width.isNotEmpty() }) {
                _errors.value = be
                return@launch
            }

            uiMetaService.saveUIMetaConfigs(_uiMetaConfigs.value).catchAndCollect {
                ConfigUiEffect.SaveSuccess.trigger()
            }
        }
    }

    init {
        loadFiledConfigs()
    }

    private fun loadFiledConfigs() {
        screenModelScope.launch {
            uiMetaService.getUIMetaConfigs().catchAndCollect {
                _uiMetaConfigs.value = it
                _errors.value = MutableList(it.size) { UIMetaConfig.Error() }
            }
        }
    }
}