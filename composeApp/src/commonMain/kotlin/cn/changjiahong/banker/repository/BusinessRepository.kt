package cn.changjiahong.banker.repository

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.FieldOverrideBinding
import cn.changjiahong.banker.model.RelBizUIMetaConfig
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {

    suspend fun findBusinessTypes(): Flow<List<Business>>

    suspend fun insertTemplateIntoBusiness(businessId: Long, templateId: Long): Long
    suspend fun deleteTemplateFromBusiness(bId: Long, tid: Long)

    suspend fun insertBusiness(name: String): Long
    suspend fun updateBusinessById(name: String, bid: Long)
    fun newFieldOverrideBinding(
        fieldId: Long,
        metaId: Long?,
        bId: Long,
        tId: Long,
        fixed: Boolean,
        fixedValue: String
    ): Long

    fun updateFieldOverrideBindingById(
        fieldId: Long,
        metaId: Long?,
        fixed: Boolean,
        fixedValue: String,
        id: Long
    )

    fun deleteFieldOverrideBindingById(id: Long)
    fun findFieldOverrideBindings(bid: Long, tid: Long): List<FieldOverrideBinding>


    fun findBizInvolvedUIMeta(bid: Long): List<Long>
    fun newRelBizUIMeta(
        bid: Long, metaId: Long, weight: Long, tag: String,
        tagWeight: Long
    ): Long

    fun deleteRelBizUIMetaById(id: Long)
    fun findBizAndUIMetaRelList(bid: Long): List<RelBizUIMetaConfig>
    fun updateRelBizUIMetaById(
        weight: Long, tag: String,
        tagWeight: Long, id: Long
    )

}