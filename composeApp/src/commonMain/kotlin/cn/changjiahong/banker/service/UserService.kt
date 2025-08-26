package cn.changjiahong.banker.service

import cn.changjiahong.banker.User
import cn.changjiahong.banker.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserService {

    suspend fun getUsers(): Flow<List<User>>

    suspend fun getUserInfos(bid: Long): Flow<List<UserInfo>>

}