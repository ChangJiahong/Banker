package cn.changjiahong.banker.repository.impl

import androidx.compose.runtime.mutableStateMapOf
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.User
import cn.changjiahong.banker.UserExtendField
import cn.changjiahong.banker.UserExtendFieldValue
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import cn.changjiahong.banker.utils.one
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

    override fun insertUser(
        name: String,
        idNumber: String,
        phone: String,
        businessRelated: BusinessRelated
    ): Long {
        val id = getSnowId()
        userQueries.insertUser(id, name, idNumber, phone, businessRelated.flag).ck()
        return id
    }


    override suspend fun findUserById(userId: Long): Flow<User> {
        return userQueries.selectById(userId).asFlow().one()
    }

    /**
     * 返回属性值，不校验属性值完整性，为null的属性值不返回
     */
    override suspend fun findUserFieldsMapById(userId: Long): Flow<Map<UserExtendField, UserExtendFieldValue>> {

        return userQueries.selectUserFieldsMapById(userId)
        { id, uid, fieldName, fieldType, description, validationRule, created, UEFV_id, UEFV_uid, fieldId, fieldValue, created_ ->
            val uef =
                UserExtendField(id, uid, fieldName, fieldType, description, validationRule, created)

            val uefv = UserExtendFieldValue(UEFV_id, UEFV_uid, fieldId, fieldValue, created_)

            Pair(uef, uefv)
        }.asFlow().list().map {
            val map = mutableStateMapOf<UserExtendField, UserExtendFieldValue>()
            it.forEach { (key, value) ->
                map.put(key, value)
            }
            map
        }
    }

    private val map: (User) -> UserDO =
        { (id, name, idNumber, phone, businessRelated, created) ->
            UserDO(
                id,
                name,
                idNumber,
                phone,
                BusinessRelated(businessRelated),
                created = Instant.fromEpochSeconds(created)
            )
        }
}