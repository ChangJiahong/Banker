package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.service.BusinessService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class BusinessServiceImpl(
    val db: BankerDb,
    val businessRepository: BusinessRepository
) : BusinessService {

    override suspend fun getBusinessList(): Flow<List<Business>> {
        return businessRepository.findBusinessTypes()
    }

    override fun addTemplate(
        businessId: Long,
        templateId: Long
    ): Flow<NoData> = flow {
        businessRepository.insertTemplateIntoBusiness(businessId, templateId)
        emit(NoData)
    }

    override fun addBusiness(name: String): Flow<NoData> = flow {
        businessRepository.insertBusiness(name)
        emit(NoData)
    }
}