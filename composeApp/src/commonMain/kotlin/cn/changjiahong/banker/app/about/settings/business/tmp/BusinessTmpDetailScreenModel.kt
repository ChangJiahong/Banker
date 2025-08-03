package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
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

sealed interface BusinessTmpDetailUiEvent : UiEvent {
    class GoTempFieldConfigScreen(val business: Business, val template: DocTemplate):UiEvent
    class GoBusinessFieldConfigScreen(val business: Business):UiEvent
}

@Factory
class BusinessTmpDetailScreenModel(val business: Business, val templateService: TemplateService) :
    MviScreenModel() {


    private val _tmpDetails = MutableStateFlow<List<DocTemplate>>(emptyList())

    val tmpDetails = _tmpDetails.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when(event){
            is BusinessTmpDetailUiEvent.GoTempFieldConfigScreen -> GoEffect(RR.FIELD_CONFIG(event.business,event.template)).trigger()
            is BusinessTmpDetailUiEvent.GoBusinessFieldConfigScreen -> GoEffect(RR.BUSINESS_FIELD_CONFIG(event.business)).trigger()
        }
    }

    init {
        loadTmpDetails()
    }

    private fun loadTmpDetails() {
        screenModelScope.launch {
            templateService.getDocTempsByBusinessId(business.id).collect {
                _tmpDetails.value = it
            }
        }
    }
}