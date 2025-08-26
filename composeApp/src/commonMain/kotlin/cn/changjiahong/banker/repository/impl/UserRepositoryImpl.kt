package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.User
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import cn.changjiahong.banker.utils.one
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class UserRepositoryImpl(db: BankerDb) : UserRepository {
    private val userQueries = db.userQueries


    override suspend fun findUsers(): List<User> {
        return userQueries.selectAll().executeAsList()
    }

    override suspend fun findAll(): Flow<List<User>> {
        return userQueries.selectAll().asFlow().list()
    }

    override fun selectById(id: Long): User {
        return userQueries.selectById(id).executeAsOne()
    }

    override fun newUser(): Long {
        val id = getSnowId()
        userQueries.insertUser(id).ck()
        return id
    }


    override suspend fun findUserById(userId: Long): Flow<User> {
        return userQueries.selectById(userId).asFlow().one()
    }

}