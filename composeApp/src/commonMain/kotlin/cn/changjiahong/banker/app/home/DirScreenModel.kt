package cn.changjiahong.banker.app.home

import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.uieffect.DIREffect
import cn.changjiahong.banker.uieffect.GoDIREffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface DirUiEvent : UiEvent {
    class ClickBusiness(val index: Int) : DirUiEvent

}

@Factory
class DirScreenModel(val businessService: BusinessService) : MviScreenModel() {

    private val _businessList = MutableStateFlow<List<Business>>(listOf())
    val businessList = _businessList.asStateFlow()

    override fun handleEvent(event: UiEvent) {

        when (event) {
            is DirUiEvent.ClickBusiness -> {
                GoDIREffect(RR.BUSINESS_HANDLER(businessList.value[event.index])).trigger()
            }
        }
    }

    init {
        loadBusiness()
    }

    private fun loadBusiness() {
        screenModelScope.launch {
            businessService.getBusinessList().collect {
                _businessList.value = it
            }
        }
    }


}