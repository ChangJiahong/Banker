package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.model.are
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.koin.core.annotation.Factory

@Factory
class UserServiceImpl(val userRepository: UserRepository) : UserService {

    override suspend fun getUsersByBR(br: BusinessRelated): Flow<List<UserDO>> {
        return userRepository.selectAll()
            .map {
                it.filter { user -> user.businessRelated are BusinessRelated.EPay }
            }
    }
}