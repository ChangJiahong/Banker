package cn.changjiahong.banker.app.about.settings.business

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.composable.VisibleState
import cn.changjiahong.banker.model.Biz
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.uieffect.GoEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory


sealed interface BusinessSettingsUiEvent : UiEvent {

    class GoBusinessTmpDetails(val business: Business) : BusinessSettingsUiEvent

    object SaveBusiness : BusinessSettingsUiEvent
    class UpdateBusiness(val biz: Biz) : BusinessSettingsUiEvent

    class ShowUpdateDialog(val biz: Biz): BusinessSettingsUiEvent
    object ShowAddDialog: BusinessSettingsUiEvent
}

@Factory
class BusinessSettingScreenModel(val businessService: BusinessService) : MviScreenModel() {

    private val _businessList = MutableStateFlow<List<Business>>(emptyList())

    val businessList = _businessList.asStateFlow()

    private val _dialogBiz = MutableStateFlow(Biz())
    val dialogBiz = _dialogBiz.asStateFlow()

    val popupVisibleState = VisibleState()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is BusinessSettingsUiEvent.GoBusinessTmpDetails -> GoEffect(RR.BUSINESS_TMP_DETAIL(event.business)).trigger()

            is BusinessSettingsUiEvent.SaveBusiness -> saveBusiness()

            is BusinessSettingsUiEvent.ShowUpdateDialog ->{
                _dialogBiz.update {
                    event.biz
                }
                popupVisibleState.show()
            }

            is BusinessSettingsUiEvent.ShowAddDialog->{
                _dialogBiz.update {
                    Biz()
                }
                popupVisibleState.show()
            }
            is BusinessSettingsUiEvent.UpdateBusiness -> {
                _dialogBiz.update {
                    event.biz
                }
            }
        }
    }

    private fun saveBusiness() {
        if (_dialogBiz.value.name.isBlank()) {
            toast("不能为空")
            return
        }
        screenModelScope.launch {
            businessService.saveBusiness(_dialogBiz.value)
                .catchAndCollect {
                    popupVisibleState.dismiss()
                    toast("保存成功")
                }
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