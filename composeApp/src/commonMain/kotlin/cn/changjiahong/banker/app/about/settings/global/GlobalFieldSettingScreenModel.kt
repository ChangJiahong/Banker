package cn.changjiahong.banker.app.about.settings.global

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.app.about.settings.ConfigUiEffect
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.model.FieldConf
import cn.changjiahong.banker.model.FieldConfError
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.FieldService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface GlobalConfigUiEvent : UiEvent {

    class Update(val index: Int, val item: FieldConf) : GlobalConfigUiEvent

}

@Factory
class GlobalFieldSettingScreenModel(
    val fieldService: FieldService
) : MviScreenModel() {

    private val _fieldConfigs = MutableStateFlow<List<FieldConf>>(emptyList())

    private val _filedErrors = MutableStateFlow<List<FieldConfError>>(emptyList())

    val filedConfigs = _fieldConfigs.asStateFlow()
    val filedErrors = _filedErrors.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is ConfigUiEvent.Add -> {
                _fieldConfigs.update {
                    it + FieldConf(bId = -1)
                }
                _filedErrors.update {
                    it + FieldConfError()
                }
            }

            is ConfigUiEvent.Delete -> {
                val field = _fieldConfigs.value[event.index]
                if (field.fieldId < 0) {
                    _fieldConfigs.update { it.toMutableList().apply { removeAt(event.index) } }
                } else {
                    _fieldConfigs.replace(
                        event.index
                    ) { field.copy(isDelete = true) }
                }
            }

            is ConfigUiEvent.Save -> saveConfig()

            is GlobalConfigUiEvent.Update -> _fieldConfigs.replace(
                event.index
            ) { event.item }
        }
    }

    private fun saveConfig() {
        screenModelScope.launch {
            val bf = filedConfigs.value
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
                _filedErrors.value = be
                return@launch
            }

            fieldService.saveGlobalFieldConfigs(_fieldConfigs.value).catchAndCollect {
                ConfigUiEffect.SaveSuccess.trigger()
            }
        }
    }

    init {
        loadFiledConfigs()
    }

    private fun loadFiledConfigs() {
        screenModelScope.launch {
            fieldService.getGlobalFieldConfigs().catchAndCollect {
                _filedErrors.value =
                    MutableList(it.size) {
                        FieldConfError()
                    }

                _fieldConfigs.value = it.map { field ->
                    field.run {
                        FieldConf(
                            fieldId,
                            -1,
                            fieldName,
                            fieldType,
                            alias,
                            width.toInt(),
                            options?:"",
                            validationRule,
                            forced == 1L
                        )
                    }
                }
            }
        }
    }
}