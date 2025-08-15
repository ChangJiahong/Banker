package cn.changjiahong.banker.service

import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.RelBasicFieldTplField
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TUExtendField
import cn.changjiahong.banker.model.UExtendField
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.model.UserField
import kotlinx.coroutines.flow.Flow

interface UserService {

    suspend fun getUsersByBR(br: BusinessRelated): Flow<List<UserDO>>

    suspend fun getUserFieldsByBusinessId(id: Long): Flow<List<BasicField>>


    suspend fun saveUFieldConfigs(value: List<UExtendField>): Flow<NoData>
    suspend fun getUserExtendFields(): Flow<List<BasicField>>

    suspend fun getUserFields(): Flow<List<UserField>>
    suspend fun saveUserTempFieldConfig(businessId: Long,value: List<TUExtendField>): Flow<NoData>
    suspend fun getFieldConfigMapByTIdAndBId(templateId: Long, businessId: Long): Flow<List<RelBasicFieldTplField>>

}