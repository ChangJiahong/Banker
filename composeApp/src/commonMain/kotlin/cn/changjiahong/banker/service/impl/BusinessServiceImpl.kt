package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.Biz
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.utils.okFlow
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

    override fun removeTemplate(
        bId: Long,
        tid: Long
    ): Flow<NoData> = okFlow {
        businessRepository.deleteTemplateFromBusiness(bId, tid)
    }

    override fun saveBusiness(biz: Biz): Flow<NoData> = okFlow {
        if (biz.id < 0) {
            businessRepository.insertBusiness(biz.name)
        } else {
            businessRepository.updateBusinessById(biz.name, biz.id)
        }
    }
}