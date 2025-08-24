package cn.changjiahong.banker.repository

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BizField
import cn.changjiahong.banker.RelBizFieldTplField
import cn.changjiahong.banker.model.FieldValuePair
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    suspend fun findBusinessTypes(): Flow<List<Business>>
    suspend fun findFieldsByBusinessId(businessId: Long): Flow<List<BizField>>
    fun insertBusinessFieldValues(uid: Long, businessId: Long, fieldValues: Map<Long, String>)

    suspend fun findFieldMapById(
        businessId: Long,
        userId: Long
    ): Map<String, FieldValuePair>

    fun findFieldsById(businessId: Long): Flow<List<BizField>>

    fun insertRelTplFieldBizField(
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String
    ): Long

    fun updateRelTplFieldBizField(
        id: Long,
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String
    )

    fun findFieldConfigMapByBidAndTid(bId: Long, tId: Long): Flow<List<RelBizFieldTplField>>
    suspend fun insertTemplateIntoBusiness(businessId: Long, templateId: Long): Long
    suspend fun insertBusiness(name: String): Long

    fun insertBizField(
        fieldName: String,
        businessId: Long,
        fieldType: String,
        description: String,
        validationRule: String,
        fixed: Boolean,
        fixedValue: String
    ): Long

    fun updateBizField(
        id: Long,
        fieldName: String,
        businessId: Long,
        fieldType: String,
        description: String,
        validationRule: String,
        fixed: Boolean,
        fixedValue: String
    )

    fun updateFieldValue(fieldValueId: Long, fieldValue: String)
    fun newFieldValue(uid: Long, businessId: Long, fieldId: Long, fieldValue: String): Long

}