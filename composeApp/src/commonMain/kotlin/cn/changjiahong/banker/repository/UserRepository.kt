package cn.changjiahong.banker.repository

import cn.changjiahong.banker.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun findUsers(): List<User>

    suspend fun findAll(): Flow<List<User>>

    fun selectById(id: Long): User

    fun newUser(): Long


    suspend fun findUserById(userId: Long): Flow<User>


}