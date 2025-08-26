package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class BusinessRepositoryImpl(db: BankerDb) : BusinessRepository {
    private val businessQueries = db.businessQueries
    private val relBizTplQueries = db.relBizTplQueries

    override suspend fun findBusinessTypes(): Flow<List<Business>> {
        return businessQueries.selectAll().asFlow().list()
    }


    override suspend fun insertTemplateIntoBusiness(businessId: Long, templateId: Long): Long {

        val re = relBizTplQueries.selectByBusinessAndTemplate(businessId, templateId)
            .executeAsOneOrNull()
        if (re != null) {
            throw ExecuteError("已添加该模版，请勿重复添加")
        }
        val id = getSnowId()
        relBizTplQueries.insert(id, businessId, templateId).ck()
        return id
    }

    override suspend fun insertBusiness(name: String): Long {
        val re = businessQueries.selectByName(name).executeAsOneOrNull()
        if (re != null) {
            throw ExecuteError("已有该业务名称，请勿重复添加")
        }
        val id = getSnowId()
        businessQueries.insert(id, name).ck()
        return id
    }

}