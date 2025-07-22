package cn.changjiahong.banker.app.template

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.TemplateField
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.TemplateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class TemplateScreenModel(
    val businessId: Long,
    val template: DocTemplate,
    val userId: Long,
    val templateService: TemplateService
) : MviScreenModel() {


    private val _templateFillerData = MutableStateFlow<List<TemplateFillerItem>>(emptyList())
    val templateFillerData = _templateFillerData.asStateFlow()


    override fun handleEvent(event: UiEvent) {

    }

    init {
        loadFields()
    }

    private fun loadFields() {
        screenModelScope.launch {
            templateService.getTemplateFillerData(businessId,template.id,userId).catch {
                it.printStackTrace()
                println(it.message)
            }.collect {
                _templateFillerData.value = it
            }
        }
    }


}