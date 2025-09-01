package cn.changjiahong.banker.repository

import cn.changjiahong.banker.Business
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    suspend fun findBusinessTypes(): Flow<List<Business>>

    suspend fun insertTemplateIntoBusiness(businessId: Long, templateId: Long): Long
    suspend fun deleteTemplateFromBusiness(bId: Long, tid: Long)

    suspend fun insertBusiness(name: String): Long


}