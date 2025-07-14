package cn.changjiahong.banker.repository

import cn.changjiahong.banker.User
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
        address: String,
        businessRelated: BusinessRelated
    ): Long

    fun getLastUserId(): Long

    fun updateUser(user: User)

    fun deleteById(id: Long)

    fun deleteAll()
}