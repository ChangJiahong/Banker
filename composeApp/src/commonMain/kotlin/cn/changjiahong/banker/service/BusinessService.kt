package cn.changjiahong.banker.service

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.NoData
import kotlinx.coroutines.flow.Flow

interface BusinessService {
    suspend fun getBusinessList(): Flow<List<Business>>

    fun addTemplate(businessId: Long, templateId: Long): Flow<NoData>
    fun addBusiness(name: String): Flow<NoData>

}
