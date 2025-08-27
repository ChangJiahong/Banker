package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.TplField
import cn.changjiahong.banker.model.BError
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.model.TplFieldConfig
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.repository.TemplateRepository
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.storage.Storage
import cn.changjiahong.banker.storage.platformFile
import cn.changjiahong.banker.tplview.TemplateKit
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

    override suspend fun getDocTempsByBusinessId(businessId: Long): Flow<List<Template>> {
        return templateRepository.findTemplatesByBusinessId(businessId)
    }

    override suspend fun getFieldsByTemplateId(id: Long): Flow<List<TplField>> {
        return templateRepository.findTemplateFieldsById(id)
    }

    override fun saveOrUpdateFieldsConfig(
        templateId: Long,
        fieldConfigs: List<TplFieldConfig>
    ): Flow<NoData> = flow {
        db.transaction {
            fieldConfigs.forEach { tempField ->
                if (tempField.id < 0) {
                    templateRepository.insertNewTemplateField(
                        templateId,
                        tempField.fieldName!!,
                        tempField.alias!!,
                        tempField.fieldType!!
                    )
                } else {
                    templateRepository.updateTemplateFieldById(
                        tempField.fieldName!!,
                        tempField.fieldType!!,
                        tempField.alias,
                        tempField.id
                    )
                }
            }
        }

        emit(NoData)
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