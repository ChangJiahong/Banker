package cn.changjiahong.banker.service

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.Biz
import cn.changjiahong.banker.model.FieldOverrideBindingConfig
import cn.changjiahong.banker.model.RelBizUIMetaConfig
import cn.changjiahong.banker.utils.FlowList
import cn.changjiahong.banker.utils.NoData
import cn.changjiahong.banker.utils.OkFlow
import kotlinx.coroutines.flow.Flow

interface BusinessService {
    suspend fun getBusinessList(): Flow<List<Business>>

    fun addTemplate(businessId: Long, templateId: Long): Flow<NoData>
    fun saveBusiness(biz: Biz): Flow<NoData>

    fun removeTemplate(bId: Long, tid: Long): Flow<NoData>
    fun saveFieldOverrideBindingConfig(configs: List<FieldOverrideBindingConfig>): OkFlow
    fun getFieldOverrideBindingConfigs(bid: Long, tid: Long): FlowList<FieldOverrideBindingConfig>

    fun autoGenerateBizAndUIMetaRelList(bid: Long): OkFlow

    fun getBizAndUIMetaRelList(bid: Long): FlowList<RelBizUIMetaConfig>
    fun saveBizAndUIMetaRelConfigs(configs: List<RelBizUIMetaConfig>): OkFlow

}
