package cn.changjiahong.banker.app.about.settings.business.tmp

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.uieffect.GoEffect
import cn.changjiahong.banker.uieffect.ShowToast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface BusinessTmpDetailUiEvent : UiEvent {
    class GoTempFieldConfigScreen(val business: Business, val template: Template) :
        BusinessTmpDetailUiEvent

    class GoBusinessFieldConfigScreen(val business: Business) : BusinessTmpDetailUiEvent

    class AddTemplate(val template: Template) : BusinessTmpDetailUiEvent

    class FuzzySearch(val name: String) : BusinessTmpDetailUiEvent
}

sealed interface BusinessTmpDetailUiEffect : UiEffect {
    object AddTempSuccess : BusinessTmpDetailUiEffect
}

@Factory
class BusinessTmpDetailScreenModel(
    val business: Business, val templateService: TemplateService,
    val businessService: BusinessService
) :
    MviScreenModel() {


    private val _tmpDetails = MutableStateFlow<List<Template>>(emptyList())

    val tmpDetails = _tmpDetails.asStateFlow()

    private val _searchRes = MutableStateFlow<List<Template>>(emptyList())

    val searchRes = _searchRes.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is BusinessTmpDetailUiEvent.GoTempFieldConfigScreen -> GoEffect(
                RR.FIELD_CONFIG(
                    event.business,
                    event.template
                )
            ).trigger()

            is BusinessTmpDetailUiEvent.GoBusinessFieldConfigScreen -> GoEffect(
                RR.BUSINESS_FIELD_CONFIG(
                    event.business
                )
            ).trigger()

            is BusinessTmpDetailUiEvent.AddTemplate -> addTemplate(event.template)

            is BusinessTmpDetailUiEvent.FuzzySearch -> fuzzySearchByTempName(event.name)
        }
    }

    private fun addTemplate(template: Template) {
        screenModelScope.launch {
            businessService.addTemplate(business.id, template.id)
                .catch {
                    ShowToast(it.message?:"").trigger()
                }.collect {
                BusinessTmpDetailUiEffect.AddTempSuccess.trigger()
            }
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


    private fun fuzzySearchByTempName(tempName: String) {
        screenModelScope.launch {
            templateService.fuzzySearchByTempName(tempName).collect {
                _searchRes.value = it
                println(it)
            }
        }
    }
}