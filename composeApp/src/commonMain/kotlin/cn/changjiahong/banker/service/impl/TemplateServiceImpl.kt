package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.TplField
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TempField
import cn.changjiahong.banker.model.TemplateFillerItem
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
    val templateRepository: TemplateRepository,
    val businessRepository: BusinessRepository,
    val userRepository: UserRepository
) : TemplateService {

    override suspend fun getAllDocTemps(): Flow<List<Template>> {
        return templateRepository.findAllDocTemps()
    }

    override suspend fun getDocTempsByBusinessId(businessId: Long): Flow<List<Template>> {
        return templateRepository.findTemplatesByBusinessId(businessId)
    }

    override suspend fun checkTemplateFillerDataIsComplete(
        businessId: Long,
        templateId: Long,
        userId: Long
    ): Flow<Boolean> = flow {

        val businessFieldsMap = businessRepository.findFieldMapById(businessId, userId)

        val userFieldsMap = userRepository.findFieldMapById(userId)

        val fieldsMap = businessFieldsMap + userFieldsMap

        val templateFields =
            templateRepository.findTemplateFieldsById2(templateId)

        templateFields.forEach { tempField ->
            if (!fieldsMap.contains(tempField.sourceFieldName)) {
                emit(false)
                return@flow
            }
        }
        emit(true)
    }

    override suspend fun getTemplateFillerData(
        businessId: Long,
        templateId: Long,
        userId: Long
    ): Flow<List<TemplateFillerItem>> = flow {

        val businessFieldsMap = businessRepository.findFieldMapById(businessId, userId)

        val userFieldsMap = userRepository.findFieldMapById(userId)

        val fieldsMap = businessFieldsMap + userFieldsMap

        val templateFields =
            templateRepository.findTemplateFieldsById2(templateId)

        val tempFillerList = mutableListOf<TemplateFillerItem>()

        templateFields.forEach { tempField ->

            if (fieldsMap.contains(tempField.formFieldName)) {
                tempFillerList.add(
                    TemplateFillerItem(
                        tempField.formFieldName,
                        tempField.sourceFieldType,
                        fieldsMap[tempField.sourceFieldName]!!.fieldValue
                    )
                )
            } else {
                throw ExecuteError("必要的属性缺失，请完善相关信息")
            }

        }
        emit(tempFillerList)
    }

    override suspend fun getFieldsByTemplateId(id: Long): Flow<List<TplField>> {
        return templateRepository.findTemplateFieldsById(id)
    }

    override fun saveOrUpdateFieldsConfig(
        templateId: Long,
        fieldConfigs: List<TempField>
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
                        tempField.alias!!,
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