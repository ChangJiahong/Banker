package cn.changjiahong.banker.repository

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFiledTemplateFiledMap
import cn.changjiahong.banker.model.BField
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.FieldValuePair
import cn.changjiahong.banker.model.TBField
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    suspend fun findBusinessTypes(): Flow<List<Business>>
    suspend fun findFieldsByBusinessId(): Flow<BusinessFields>
    fun insertBusinessFieldValues(uid: Long, businessId: Long, fieldValues: Map<Long, String>)

    suspend fun findFieldMapById(
        businessId: Long,
        userId: Long
    ): Map<String, FieldValuePair>

    fun findFieldsById(businessId: Long): Flow<List<BusinessField>>

    fun insertBusinessFields(insertData: List<BField>)

    fun updateBusinessFields(updateData: List<BField>)

    fun saveOrUpdateBusinessFields(fields: List<BusinessField>)

    fun insertBusinessTemplateFieldMap(
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String?
    ): Long

    fun updateBusinessTemplateFieldMap(
        id: Long,
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String?
    )

    fun findFieldConfigMapByBidAndTid(bId: Long, tId: Long): Flow<List<BusinessFiledTemplateFiledMap>>
    suspend fun insertTemplateIntoBusiness(businessId: Long, templateId: Long):Long
    suspend fun insertBusiness(name: String): Long

}