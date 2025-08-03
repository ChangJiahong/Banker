package cn.changjiahong.banker.service

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFiledTemplateFiledMap
import cn.changjiahong.banker.model.BField
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TBField
import kotlinx.coroutines.flow.Flow

interface BusinessService {
    suspend fun getBusinessList(): Flow<List<Business>>

    suspend fun getFieldsByBusinessId(businessId: Long): Flow<BusinessFields>

    /**
     * 通过业务id和模版id，获取模版对应的业务属性
     */
    suspend fun getFieldsById(businessId: Long,templateId:Long): Flow<List<BusinessField>>

    suspend fun getFieldsById(businessId: Long): Flow<List<BusinessField>>



    suspend fun saveBusinessWithUserValue(
        username: String,
        idNumber: String,
        phone: String,
        businessId: Long,
        fieldValues: Map<Long, String>
    ): Flow<NoData>

    suspend fun saveBFieldConfigs(value: List<BField>): Flow<NoData>

    fun saveBusinessTemplateFieldConfig(data: List<TBField>): Flow<NoData>

    fun getFieldConfigMapByBidAndTid(bId: Long, tId: Long): Flow<List<BusinessFiledTemplateFiledMap>>

}
