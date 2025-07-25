package cn.changjiahong.banker.repository

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFieldValue
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.FieldValuePair
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    suspend fun findBusinessTypes(): Flow<List<Business>>
    suspend fun findFieldsByBusinessId(): Flow<BusinessFields>
    fun insertBusinessFieldValues(uid: Long, businessId: Long, fieldValues: Map<Long, String>)

    suspend fun findFieldMapById(
        businessId: Long,
        userId: Long
    ): Map<String,FieldValuePair>

}