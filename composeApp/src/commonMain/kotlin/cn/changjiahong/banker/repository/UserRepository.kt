package cn.changjiahong.banker.repository

import cn.changjiahong.banker.User
import cn.changjiahong.banker.UserExtendField
import cn.changjiahong.banker.UserExtendFieldValue
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun selectAll(): Flow<List<UserDO>>

    fun selectById(id: Long): User

    fun insertUser(
        name: String,
        idNumber: String,
        phone: String,
        businessRelated: BusinessRelated
    ): Long


    suspend fun findUserById(userId: Long): Flow<User>


   suspend fun findUserFieldsMapById(userId: Long): Flow<Map<UserExtendField, UserExtendFieldValue>>
}