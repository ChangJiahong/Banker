package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.User
import cn.changjiahong.banker.model.UserInfo
import cn.changjiahong.banker.repository.FieldRepository
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class UserServiceImpl(
    val db: BankerDb,
    val userRepository: UserRepository,
    val fieldRepository: FieldRepository
) : UserService {

    override suspend fun getUsers(): Flow<List<User>> {
        return userRepository.findAll()

    }


    override suspend fun getUserInfos(bid: Long): Flow<List<UserInfo>> = flow {

        val users = userRepository.findUsers()
        val userinfos = mutableListOf<UserInfo>()
        users.forEach { (uid, _) ->
            val fields = fieldRepository.findFieldsByUidAndBid(uid,bid)
                .associateBy { field -> field.fieldName }
            userinfos += UserInfo(uid, fields)
        }

        emit(userinfos)
    }
}