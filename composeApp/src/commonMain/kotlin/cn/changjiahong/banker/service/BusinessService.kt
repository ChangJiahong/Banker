package cn.changjiahong.banker.service

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.NoData
import kotlinx.coroutines.flow.Flow

interface BusinessService {
    suspend fun getBusinessList(): Flow<List<Business>>
    suspend fun getFieldsByBusinessId(businessId: Long): Flow<BusinessFields>
    suspend fun saveBusinessWithUserValue(
        username: String,
        idNumber: String,
        phone: String,
        businessId: Long,
        fieldValues: Map<Long, String>
    ): Flow<NoData>
}
