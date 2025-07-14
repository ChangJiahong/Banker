package cn.changjiahong.banker.service

import cn.changjiahong.banker.User
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import kotlinx.coroutines.flow.Flow

interface UserService {

   suspend fun getUsersByBR(br: BusinessRelated): Flow<List<UserDO>>

}