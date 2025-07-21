package cn.changjiahong.banker.service.impl

import androidx.compose.runtime.key
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFieldValue
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.TemplateField
import cn.changjiahong.banker.UserExtendField
import cn.changjiahong.banker.UserExtendFieldValue
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.repository.DocTemplateRepository
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.TemplateService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import org.koin.core.annotation.Factory
import kotlin.collections.emptyList

@Factory
class TemplateServiceImpl(
    val docTemplateRepository: DocTemplateRepository,
    val businessRepository: BusinessRepository,
    val userRepository: UserRepository
) : TemplateService {

    override suspend fun getDocTempsByBusinessId(businessId: Long): Flow<List<DocTemplate>> {
        return docTemplateRepository.findTemplatesByBusinessId(businessId)
    }

    override suspend fun checkTemplateFillerDataIsComplete(
        businessId: Long,
        templateId: Long,
        userId: Long
    ): Flow<Boolean> {
        val businessFieldsMap: Flow<Map<BusinessField, BusinessFieldValue>> =
            businessRepository.findBusinessFieldsMapById(businessId, userId)
        val userFieldsMap: Flow<Map<UserExtendField, UserExtendFieldValue>> =
            userRepository.findUserFieldsMapById(userId)
        val templateFields: Flow<List<TemplateField>> =
            docTemplateRepository.findTemplateFieldsById(templateId)
        val userDetail = userRepository.findUserById(userId)

        val finalFlow: Flow<Boolean> =
            combine(
                businessFieldsMap,userDetail,
                userFieldsMap,
                templateFields
            ) { businessFieldsMap,userDetail, userFieldsMap, templateFields ->

                templateFields.forEach { tempField ->
                    try {
                        if (tempField.sourceFieldName.startsWith("user:")) {
                            val fname = tempField.sourceFieldName.split(":")[1]

                            when (fname) {
                                "phone" -> userDetail.phone
                                "idNumber" -> userDetail.idNumber
                                "name" -> userDetail.name
                                else -> userFieldsMap.firstNotNullOf { (key, value) ->
                                    if (key.fieldName == fname) value.fieldValue else null
                                }
                            }
                        } else {
                            businessFieldsMap.firstNotNullOf { (key, value) ->
                                if (key.fieldName == tempField.sourceFieldName) value.fieldValue else null
                            }
                        }
                    } catch (e: NoSuchElementException) {
                        return@combine false
                    }
                }
                true
            }.take(1)

        return finalFlow
    }

    override suspend fun getTemplateFillerData(
        businessId: Long,
        templateId: Long,
        userId: Long
    ): Flow<List<TemplateFillerItem>> {
        val businessFieldsMap: Flow<Map<BusinessField, BusinessFieldValue>> =
            businessRepository.findBusinessFieldsMapById(businessId, userId)
        val userDetail = userRepository.findUserById(userId)
        val userFieldsMap: Flow<Map<UserExtendField, UserExtendFieldValue>> =
            userRepository.findUserFieldsMapById(userId)

        val templateFields: Flow<List<TemplateField>> =
            docTemplateRepository.findTemplateFieldsById(templateId)

        val finalFlow: Flow<List<TemplateFillerItem>> =
            combine(
                businessFieldsMap,
                userDetail,
                userFieldsMap,
                templateFields
            ) { businessFieldsMap, userDetail, userFieldsMap, templateFields ->
                Triple(businessFieldsMap, userFieldsMap, templateFields)
                val tempFillerList = mutableListOf<TemplateFillerItem>()
                val uMap = mutableMapOf<String, String>()
                userFieldsMap.forEach { (key, value) -> uMap.put(key.fieldName, value.fieldValue) }

                templateFields.forEach { tempField ->
                    val fieldName: String = tempField.formFieldName
                    val fieldType: String = tempField.sourceFieldType
                    try {
                        val fieldValue: String =
                            if (tempField.sourceFieldName.startsWith("user:")) {
                                val fname = tempField.sourceFieldName.split(":")[1]

                                when (fname) {
                                    "phone" -> userDetail.phone
                                    "idNumber" -> userDetail.idNumber
                                    "name" -> userDetail.name
                                    else -> userFieldsMap.firstNotNullOf { (key, value) ->
                                        if (key.fieldName == fname) value.fieldValue else null
                                    }
                                }

                            } else {
                                businessFieldsMap.firstNotNullOf { (key, value) ->
                                    if (key.fieldName == tempField.sourceFieldName) value.fieldValue else null
                                }
                            }
                        tempFillerList.add(TemplateFillerItem(fieldName, fieldType, fieldValue))
                    } catch (e: NoSuchElementException) {
                        throw ExecuteError("模版文件属性值缺失，请完善相关信息！")
                    }
                }

                tempFillerList

            }.take(1)

        return finalFlow
    }
}