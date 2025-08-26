package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.TplField
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.model.TplFieldConfig
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.repository.TemplateRepository
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.TemplateService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class TemplateServiceImpl(
    val db: BankerDb,
    val templateRepository: TemplateRepository
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
}