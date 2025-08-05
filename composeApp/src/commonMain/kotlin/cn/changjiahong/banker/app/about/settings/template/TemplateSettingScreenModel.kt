package cn.changjiahong.banker.app.about.settings.template

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.uieffect.GoEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface TempSettingUiEvent : UiEvent {
    class GoPreTemplateScreen(val docTemplate: DocTemplate) : TempSettingUiEvent
    class GoTempFiledSettingScreen(val docTemplate: DocTemplate) : TempSettingUiEvent
}

@Factory
class TemplateSettingScreenModel(val templateService: TemplateService) : MviScreenModel() {

    private val _tempFiles = MutableStateFlow<List<DocTemplate>>(emptyList())

    val tempFiles = _tempFiles.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is TempSettingUiEvent.GoPreTemplateScreen -> GoEffect(RR.PRE_TEMPLATE(event.docTemplate)).trigger()
            is TempSettingUiEvent.GoTempFiledSettingScreen -> GoEffect(RR.TEMP_FIELD_SETTING(event.docTemplate)).trigger()
        }
    }

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        screenModelScope.launch {

            templateService.getAllDocTemps().collect {
                _tempFiles.value = it
            }

        }
    }

}