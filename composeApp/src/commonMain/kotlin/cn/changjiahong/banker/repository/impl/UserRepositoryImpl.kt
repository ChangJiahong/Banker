package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.User
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory

@Factory
class UserRepositoryImpl(db: BankerDb) : UserRepository {
    private val userQueries = db.userQueries

    override suspend fun selectAll(): Flow<List<UserDO>> {
        return userQueries.selectAll().asFlow().list().map { it.map(map) }
    }

    override fun selectById(id: Long): User {
        return userQueries.selectById(id).executeAsOne()
    }

    override fun updateUser(user: User) {
        user.apply {
            userQueries.updateUser(name, idNumber, phone, address, businessRelated, id)
        }
    }

    override fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun insertUser(
        name: String,
        idNumber: String,
        phone: String,
        address: String,
        businessRelated: BusinessRelated
    ): Long {
        val id = getSnowId()
        userQueries.insertUser(id, name, idNumber, phone, address, businessRelated.flag).ck()
        return id
    }

    override fun getLastUserId(): Long {
        return userQueries.lastInsertRowId().executeAsOne()
    }

    private val map: (User) -> UserDO =
        { (id, name, idNumber, phone, address, businessRelated, created) ->
            UserDO(
                id,
                name,
                idNumber,
                phone,
                address,
                BusinessRelated(businessRelated),
                created = Instant.fromEpochMilliseconds(created)
            )
        }
}