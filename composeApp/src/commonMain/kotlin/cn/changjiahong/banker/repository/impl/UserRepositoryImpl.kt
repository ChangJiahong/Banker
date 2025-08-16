package cn.changjiahong.banker.repository.impl

import androidx.compose.runtime.mutableStateMapOf
import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.User
import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.BasicFieldValue
import cn.changjiahong.banker.RelBasicFieldTplField
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.FieldValuePair
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import cn.changjiahong.banker.utils.one
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import org.koin.core.annotation.Factory

@Factory
class UserRepositoryImpl(db: BankerDb) : UserRepository {
    private val userQueries = db.userQueries
    private val userExtendFieldQueries = db.basicFieldQueries
    private val userFieldTempFieldMapQueries = db.relBasicFieldTplFieldQueries
    private val userExtendFieldValueQueries = db.basicFieldValueQueries

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

    override suspend fun findFieldMapById(userId: Long): Map<String, FieldValuePair> {
        val userDetail = selectById(userId)
        val userFields = userExtendFieldQueries.selectUserFieldsByUid().executeAsList()
        val userFieldValues =
            userExtendFieldValueQueries.selectUserFieldValuesByUid(userId).executeAsList()
        val fieldMap = mutableMapOf<String, FieldValuePair>()
        fieldMap.apply {

            userFields.forEach { field ->
                val fv = userFieldValues.find { it.fieldId == field.id }
                if (fv != null) {
                    put(
                        field.fieldName,
                        FieldValuePair(
                            field.id,
                            field.fieldName,
                            "field.fieldType",
                            field.description,
                            fv.fieldValue
                        )
                    )
                }
            }
        }

        return fieldMap
    }

    /**
     * 返回属性值，不校验属性值完整性，为null的属性值不返回
     */
    override suspend fun findUserFieldsMapById(userId: Long): Flow<Map<BasicField, BasicFieldValue>> {

        return flow {  }
    }

    override fun insertUserExtendField(
        fieldName: String,
        description: String,
        forced: Boolean,
        validationRule: String
    ): Long {
        val id = getSnowId()
        userExtendFieldQueries.insert(
            id,
            fieldName,
            if (forced) 1 else 0,
            description,
            validationRule
        ).ck()
        return id
    }

    override fun updateUserExtendFieldById(
        fieldName: String,
        description: String,
        forced: Boolean,
        validationRule: String,
        id: Long
    ) {
        userExtendFieldQueries.update(
            fieldName,
            if (forced) 1 else 0,
            description,
            validationRule,
            id
        ).ck()
    }

    override suspend fun findUserExtendFields(): Flow<List<BasicField>> {
        return userExtendFieldQueries.selectUserFields().asFlow().list()
    }

    override fun findUserExtFields(): List<BasicField> {
        return userExtendFieldQueries.selectUserFields().executeAsList()
    }

    override fun insertUserTempFieldMap(
        businessId: Long,
        tempFieldId: Long,
        userFieldId: Long
    ): Long {
        val id = getSnowId()
        userFieldTempFieldMapQueries.insert(id, userFieldId, tempFieldId, businessId).ck()
        return id
    }

    override fun updateUserTempFieldMap(
        id: Long,
        userFieldId: Long,
        tempFieldId: Long
    ) {
        userFieldTempFieldMapQueries.update(userFieldId, tempFieldId, id).ck()
    }

    override suspend fun findFieldConfigMapByTid(
        templateId: Long,
        businessId: Long
    ): List<RelBasicFieldTplField> {
        return userFieldTempFieldMapQueries.findFieldConfigMapByTid(templateId, businessId)
            .executeAsList()
    }

    override suspend fun findUserFieldsByBusinessId(businessId: Long): Flow<List<BasicField>> {
        return userExtendFieldQueries.findUserFieldsByBId(businessId).asFlow().list()
    }

}