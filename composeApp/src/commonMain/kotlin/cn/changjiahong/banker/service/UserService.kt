package cn.changjiahong.banker.service

import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.RelBasicFieldTplField
import cn.changjiahong.banker.User
import cn.changjiahong.banker.model.BasicFieldConfig
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.RelTplFieldBasicFieldConfig
import cn.changjiahong.banker.model.UserField
import cn.changjiahong.banker.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserService {

    suspend fun getUsers(): Flow<List<User>>

    suspend fun getUserFieldsByBusinessId(id: Long): Flow<List<BasicField>>


    suspend fun saveUFieldConfigs(value: List<BasicFieldConfig>): Flow<NoData>
    suspend fun getUserExtendFields(): Flow<List<BasicField>>

    suspend fun getUserFields(): Flow<List<UserField>>
    suspend fun saveRelTplFieldBasicFieldConfig(
        businessId: Long,
        value: List<RelTplFieldBasicFieldConfig>
    ): Flow<NoData>

    suspend fun getFieldConfigMapByTIdAndBId(
        templateId: Long,
        businessId: Long
    ): Flow<List<RelBasicFieldTplField>>

    suspend fun getUserInfos(): Flow<List<UserInfo>>

}