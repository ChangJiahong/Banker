package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.TplField
import cn.changjiahong.banker.model.BError
import cn.changjiahong.banker.utils.NoData
import cn.changjiahong.banker.model.TplFieldConfig
import cn.changjiahong.banker.repository.TemplateRepository
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.storage.Storage
import cn.changjiahong.banker.storage.platformFile
import cn.changjiahong.banker.tplview.TemplateKit
import cn.changjiahong.banker.utils.FlowList
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import cn.changjiahong.banker.utils.toId
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class TemplateServiceImpl(
    val db: BankerDb,
    val templateRepository: TemplateRepository,
    val fieldService: FieldService
) : TemplateService {

    override suspend fun getAllDocTemps(): Flow<List<Template>> {
        return templateRepository.findAllDocTemps()
    }

    override suspend fun getDocTempsByBusinessId(businessId: Long): Flow<List<Template>> =
        returnFlow {
            templateRepository.findTemplatesByBusinessId(businessId)
        }

    override suspend fun getFieldsByTemplateId(id: Long): Flow<List<TplField>> = returnFlow {
        templateRepository.findTemplateFieldsById(id)
    }

    override suspend fun getFieldConfigsByTid(tid: Long): FlowList<TplFieldConfig> = returnFlow {
        templateRepository.findTemplateFieldsById(tid).map { TplFieldConfig(it) }
    }

    override fun saveFieldConfigs(
        templateId: Long,
        fieldConfigs: List<TplFieldConfig>
    ): Flow<NoData> = okFlow {
        db.transaction {
            fieldConfigs.forEach { tempField ->
                tempField.run {
                    if (fieldId < 0 && !isDelete) {
                        templateRepository.insertNewTemplateField(
                            templateId,
                            formFieldName,
                            formFieldType,
                            metaId.toId(),
                            isFixed,
                            fixedValue
                        )
                    } else if (!tempField.isDelete) {
                        templateRepository.updateTemplateFieldById(
                            formFieldName,
                            formFieldType,
                            metaId.toId(),
                            isFixed,
                            fixedValue,
                            fieldId
                        )
                    } else {
                        templateRepository.deleteTemplateFieldById(fieldId)
                    }
                }
            }
        }
    }

    override suspend fun fuzzySearchByTempName(tempName: String): Flow<List<Template>> {
        return templateRepository.findTemplatesByFuzzyName(tempName)
    }

    override suspend fun addNewTemplate(
        path: String,
        templateName: String,
        fileType: String
    ): Flow<NoData> = flow {
        templateRepository.insertNewTemplate(templateName, path, fileType)
        emit(NoData)
    }

    override suspend fun deleteTemplate(tid: Long): Flow<NoData> = okFlow {
        templateRepository.deleteTemplate(tid)

    }

    override suspend fun fillFromToTemplate(
        userId: Long,
        businessId: Long,
        template: Template
    ): Flow<PlatformFile> = flow {
        fieldService.getTplFieldVals(userId, businessId, template.id)
            .collect { formFieldValues ->
                fieldService.getFieldConfigsForTemplate(businessId, template.id)
                    .collect { fieldConfigs ->
                        fieldConfigs.forEach { f ->
                            if (formFieldValues.none { f.tFieldId == it.tFieldId && it.fieldValue.isNotBlank() }) {
                                throw BError.Fail("信息不完整")
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
                        ).collect {
                            emit(cacheFile)
                        }
                    }

            }
    }

}