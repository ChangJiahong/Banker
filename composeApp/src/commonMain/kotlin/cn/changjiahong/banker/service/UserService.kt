package cn.changjiahong.banker.service

import cn.changjiahong.banker.User
import cn.changjiahong.banker.UserExtendField
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TUExtendField
import cn.changjiahong.banker.model.UExtendField
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.model.UserField
import kotlinx.coroutines.flow.Flow

interface UserService {

    suspend fun getUsersByBR(br: BusinessRelated): Flow<List<UserDO>>
    suspend fun saveUFieldConfigs(value: List<UExtendField>): Flow<NoData>
    suspend fun getUserExtendFields(): Flow<List<UserExtendField>>

    suspend fun getUserFields(): Flow<List<UserField>>
    suspend fun saveUserTempFieldConfig(value: List<TUExtendField>): Flow<NoData>

}