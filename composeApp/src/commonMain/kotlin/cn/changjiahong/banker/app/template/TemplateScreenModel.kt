package cn.changjiahong.banker.app.template

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.storage.Storage
import cn.changjiahong.banker.storage.platformFile
import cn.changjiahong.banker.tplview.TemplateKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class TemplateScreenModel(
    val userId: Long,
    val businessId: Long,
    val template: Template,
    val templateService: TemplateService,
    val fieldService: FieldService
) : MviScreenModel() {


    private val _templateFillerData = MutableStateFlow<List<TemplateFillerItem>>(emptyList())
    val templateFillerData = _templateFillerData.asStateFlow()

    private val _cacheFile = MutableStateFlow<PlatformFile?>(null)
    val cacheFile = _cacheFile.asStateFlow()


    override fun handleEvent(event: UiEvent) {

    }

    init {
        fillData()
    }

    private fun fillData() {

        screenModelScope.launch {


            fieldService.getTplFieldVals(userId, businessId, template.id)
                .catchAndCollect { formFieldValues ->

                    fieldService.getFieldConfigsForTemplate(businessId, template.id)
                        .catchAndCollect { fieldConfigs ->
                            fieldConfigs.forEach { f ->
                                if (formFieldValues.none { f.tFieldId == it.tFieldId && it.fieldValue.isNotBlank() }) {
                                    tip("信息不完整")
                                    return@catchAndCollect
                                }
                            }

                            val cacheFile = Storage.getCacheFile(
                                userId,
                                businessId,
                                template.filePath.platformFile.name
                            )
                            TemplateKit.fillTemplateForm(
                                formFieldValues,
                                template.filePath.platformFile,
                                cacheFile
                            ).catchAndCollect {
                                _cacheFile.value = cacheFile
                            }
                        }


                }

        }
    }


}