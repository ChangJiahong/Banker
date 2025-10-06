package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.FieldOverrideBinding
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.RelBizUIMetaConfig
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import cn.changjiahong.banker.utils.toLong
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class BusinessRepositoryImpl(db: BankerDb) : BusinessRepository {
    private val businessQueries = db.businessQueries
    private val relBizTplQueries = db.relBizTplQueries

    val fieldOverrideBindingQueries = db.fieldOverrideBindingQueries
    val relBizUIMetaQueries = db.relBizUIMetaQueries

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

    override suspend fun deleteTemplateFromBusiness(bId: Long, tid: Long) {
        relBizTplQueries.deleteTemplateFromBusiness(bId, tid).ck()
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

    override suspend fun updateBusinessById(name: String, bid: Long) {
        val re = businessQueries.selectByName(name).executeAsOneOrNull()
        if (re != null) {
            throw ExecuteError("已有该业务名称")
        }
        businessQueries.update(name, bid).ck()
    }


    override fun newFieldOverrideBinding(
        fieldId: Long,
        metaId: Long?,
        bId: Long,
        tId: Long,
        fixed: Boolean,
        fixedValue: String
    ): Long {
        val id = getSnowId()
        fieldOverrideBindingQueries.insert(
            id,
            fieldId,
            metaId,
            bId,
            tId,
            fixed.toLong(),
            fixedValue
        ).ck()
        return id
    }

    override fun updateFieldOverrideBindingById(
        fieldId: Long,
        metaId: Long?,
        fixed: Boolean,
        fixedValue: String,
        id: Long
    ) {
        fieldOverrideBindingQueries.update(fieldId, metaId, fixed.toLong(), fixedValue, id).ck()
    }

    override fun deleteFieldOverrideBindingById(id: Long) {
        fieldOverrideBindingQueries.delete(id).ck()
    }

    override fun findFieldOverrideBindings(
        bid: Long,
        tid: Long
    ): List<FieldOverrideBinding> {
        return fieldOverrideBindingQueries.select(bid, tid).executeAsList()
    }

    override fun findBizInvolvedUIMeta(bid: Long): List<Long> {
        return fieldOverrideBindingQueries.selectBizOverrideInvolvedUIMeta(bid).executeAsList()
            .filter { it.metaId != null }.map { it.metaId!! }
    }

    override fun newRelBizUIMeta(
        bid: Long,
        metaId: Long,
        weight: Long,
        tag: String,
        tagWeight: Long
    ): Long {
        val id = getSnowId()
        relBizUIMetaQueries.insert(id, bid, metaId, weight, tag, tagWeight).ck()
        return id
    }

    override fun updateRelBizUIMetaById(
        weight: Long, tag: String,
        tagWeight: Long, id: Long
    ) {
        relBizUIMetaQueries.update(weight, tag, tagWeight, id).ck()
    }

    override fun deleteRelBizUIMetaById(id: Long) {
        relBizUIMetaQueries.deleteById(id).ck()
    }

    override fun findBizAndUIMetaRelList(bid: Long): List<RelBizUIMetaConfig> {
        return relBizUIMetaQueries.selectBizAndUIMetaRelList(bid).executeAsList().map {
            RelBizUIMetaConfig(
                it.id,
                it.bId,
                it.label ?: "",
                it.metaId ?: -1,
                it.weight,
                it.tag,
                it.tagWeight
            )
        }
    }

}