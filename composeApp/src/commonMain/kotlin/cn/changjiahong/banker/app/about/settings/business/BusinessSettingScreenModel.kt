package cn.changjiahong.banker.app.about.settings.business

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.uieffect.GoEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory


sealed interface BusinessSettingsUiEvent : UiEvent {

    class GoBusinessTmpDetails(val business: Business) : BusinessSettingsUiEvent
}

@Factory
class BusinessSettingScreenModel(val businessService: BusinessService) : MviScreenModel() {

    private val _businessList = MutableStateFlow<List<Business>>(emptyList())

    val businessList = _businessList.asStateFlow()


    override fun handleEvent(event: UiEvent) {
        when (event) {
            is BusinessSettingsUiEvent.GoBusinessTmpDetails -> GoEffect(RR.BUSINESS_TMP_DETAIL(event.business)).trigger()
        }
    }


    init {
        loadBusinessList()
    }

    private fun loadBusinessList() {
        screenModelScope.launch {
            businessService.getBusinessList().collect {
                _businessList.value = it
            }
        }
    }
}