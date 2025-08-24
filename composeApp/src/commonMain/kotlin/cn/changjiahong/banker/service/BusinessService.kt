package cn.changjiahong.banker.service

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BizField
import cn.changjiahong.banker.RelBizFieldTplField
import cn.changjiahong.banker.model.BizFieldConfig
import cn.changjiahong.banker.model.FieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.RelTplFieldBizFieldConfig
import kotlinx.coroutines.flow.Flow

interface BusinessService {
    suspend fun getBusinessList(): Flow<List<Business>>

    suspend fun getFieldsByBusinessId(businessId: Long): Flow<List<BizField>>



    suspend fun saveFields(
        uId: Long?,
        businessId: Long,
        fieldValues: Map<Long, FieldValue>
    ): Flow<NoData>

    suspend fun saveBizFieldConfigs(value: List<BizFieldConfig>): Flow<NoData>

    fun saveRelTplFieldBizFieldConfig(data: List<RelTplFieldBizFieldConfig>): Flow<NoData>

    fun getFieldConfigMapByBidAndTid(bId: Long, tId: Long): Flow<List<RelBizFieldTplField>>
    fun addTemplate(businessId: Long, templateId: Long): Flow<NoData>
    fun addBusiness(name: String): Flow<NoData>

}
