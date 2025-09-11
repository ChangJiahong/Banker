package cn.changjiahong.banker.app.about.settings.template

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.about.settings.ConfigUiEvent
import cn.changjiahong.banker.composable.Option
import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.TplFieldConfig
import cn.changjiahong.banker.model.UIMetaConfig
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UIMetaService
import cn.changjiahong.banker.storage.platformFile
import cn.changjiahong.banker.tplview.TemplateKit
import cn.changjiahong.banker.utils.toId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface TFSUiEvent : UiEvent {
    object AddNewFieldConfig : TFSUiEvent

    object SaveConfig : TFSUiEvent

    class UpdateTempFieldConfig(val index: Int, val tempField: TplFieldConfig) : TFSUiEvent

    class SearchUIMetaConfig(val name: String) : TFSUiEvent
}

sealed interface TFSUiEffect : UiEffect {
    object SaveSuccess : TFSUiEffect
}

@Factory
class TempFieldConfigScreenModel(
    val template: Template,
    val templateService: TemplateService,
    val uiMetaService: UIMetaService
) :
    MviScreenModel() {

    private val _tempFieldConfigs = MutableStateFlow<List<TplFieldConfig>>(emptyList())
    private val _error = MutableStateFlow<List<TplFieldConfig.Error>>(emptyList())
    private val _tempFormFields = MutableStateFlow<List<FormField>>(emptyList())
    private val _uiMetaConfigs = MutableStateFlow<List<UIMetaConfig>>(emptyList())

    private val _searchResults = MutableStateFlow<List<Option<Long>>>(emptyList())

    val tempFieldConfigs = _tempFieldConfigs.asStateFlow()
    val error = _error.asStateFlow()

    val tempFormFields = _tempFormFields.asStateFlow()
    val uiMetaConfigs = _uiMetaConfigs.asStateFlow()
    val searchResults = _searchResults.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            is TFSUiEvent.AddNewFieldConfig -> {
                _tempFieldConfigs.update { it + TplFieldConfig() }
                _error.update { it + TplFieldConfig.Error() }
            }

            is ConfigUiEvent.Delete -> {
                val field = _tempFieldConfigs.value[event.index]
                if (field.fieldId < 0) {
                    _tempFieldConfigs.update { it.toMutableList().apply { removeAt(event.index) } }
                } else {
                    _tempFieldConfigs.replace(
                        event.index
                    ) { field.copy(isDelete = true) }
                }
            }

            is TFSUiEvent.UpdateTempFieldConfig -> {
                _tempFieldConfigs.replace(event.index) { event.tempField }
            }

            is TFSUiEvent.SearchUIMetaConfig -> {
                screenModelScope.launch {
                    uiMetaService.getUIMetaConfigsByLabel(event.name).stateIn(screenModelScope)
                        .catchAndCollect { da ->
                            _searchResults.value = da.map { Option(it.label, it.metaId) }
                        }
                }
            }

            is TFSUiEvent.SaveConfig -> saveConfig()
        }
    }

    private fun saveConfig() {

        val values = _tempFieldConfigs.value
        println(values)
        var hasE = false
        val error = mutableListOf<TplFieldConfig.Error>()
        values.forEachIndexed { index, field ->
            var fE = ""
            var tE = ""
            var aE = ""
            if (field.formFieldName.isBlank()) {
                fE = "不能为空"
                hasE = true
            }
            if (field.formFieldType.isBlank()) {
                tE = "不能为空"
                hasE = true
            }
            if (field.metaId.toId() ==null) {
                aE = "不能为空"
                hasE = true
            }

            error += TplFieldConfig.Error(fE, tE, metaId = aE)
        }

        _error.value = error

        if (hasE) return


        screenModelScope.launch {
            templateService.saveFieldConfigs(template.id, values).collect {
                TFSUiEffect.SaveSuccess.trigger()
            }
        }
    }

    init {
        loadFormFields()
        loadTempFieldConfigs()
        loadUIMetaConfigs()
    }

    private fun loadUIMetaConfigs() {
        screenModelScope.launch {
            uiMetaService.getUIMetaConfigs().catchAndCollect {
                _uiMetaConfigs.value = it
            }
        }
    }

    private fun loadFormFields() {
        screenModelScope.launch {
            TemplateKit.getFormFields(template.filePath.platformFile).catchAndCollect {
                _tempFormFields.value = it
            }
        }
    }

    private fun loadTempFieldConfigs() {
        screenModelScope.launch {
            templateService.getFieldConfigsByTid(template.id).collect { data ->
                _tempFieldConfigs.value = data
                _error.value = MutableList(data.size) { TplFieldConfig.Error() }
            }
        }
    }
}