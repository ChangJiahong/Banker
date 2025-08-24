package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.User
import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.BasicFieldValue
import cn.changjiahong.banker.RelBasicFieldTplField
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.Field
import cn.changjiahong.banker.model.FieldValuePair
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import cn.changjiahong.banker.utils.one
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class UserRepositoryImpl(db: BankerDb) : UserRepository {
    private val userQueries = db.userQueries
    private val basicFieldQueries = db.basicFieldQueries
    private val relBasicFieldTplFieldQueries = db.relBasicFieldTplFieldQueries
    private val basicFieldValueQueries = db.basicFieldValueQueries
    private val bizFieldValueQueries = db.bizFieldValueQueries

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

    override suspend fun findFieldMapById(userId: Long): Map<String, FieldValuePair> {
        val userDetail = selectById(userId)
        val userFields = basicFieldQueries.selectUserFieldsByUid().executeAsList()
//        val userFieldValues =
//            basicFieldValueQueries.selectUserFieldValuesByUid(userId).executeAsList()
        val fieldMap = mutableMapOf<String, FieldValuePair>()
//        fieldMap.apply {
//
//            userFields.forEach { field ->
//                val fv = userFieldValues.find { it.fieldId == field.id }
//                if (fv != null) {
//                    put(
//                        field.fieldName,
//                        FieldValuePair(
//                            field.id,
//                            field.fieldName,
//                            "field.fieldType",
//                            field.description,
//                            fv.fieldValue
//                        )
//                    )
//                }
//            }
//        }

        return fieldMap
    }

    /**
     * 返回属性值，不校验属性值完整性，为null的属性值不返回
     */
    override suspend fun findUserFieldsMapById(userId: Long): Flow<Map<BasicField, BasicFieldValue>> {

        return flow { }
    }

    override fun insertUserExtendField(
        fieldName: String,
        description: String,
        forced: Boolean,
        validationRule: String
    ): Long {
        val id = getSnowId()
        basicFieldQueries.insert(
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
        basicFieldQueries.update(
            fieldName,
            if (forced) 1 else 0,
            description,
            validationRule,
            id
        ).ck()
    }

    override suspend fun findUserExtendFields(): Flow<List<BasicField>> {
        return basicFieldQueries.selectUserFields().asFlow().list()
    }

    override fun findUserExtFields(): List<BasicField> {
        return basicFieldQueries.selectUserFields().executeAsList()
    }

    override fun insertUserTempFieldMap(
        businessId: Long,
        tempFieldId: Long,
        userFieldId: Long
    ): Long {
        val id = getSnowId()
        relBasicFieldTplFieldQueries.insert(id, userFieldId, tempFieldId, businessId).ck()
        return id
    }

    override fun updateUserTempFieldMap(
        id: Long,
        userFieldId: Long,
        tempFieldId: Long
    ) {
        relBasicFieldTplFieldQueries.update(userFieldId, tempFieldId, id).ck()
    }

    override suspend fun findFieldConfigMapByTid(
        templateId: Long,
        businessId: Long
    ): List<RelBasicFieldTplField> {
        return relBasicFieldTplFieldQueries.findFieldConfigMapByTid(templateId, businessId)
            .executeAsList()
    }

    override suspend fun findUserFieldsByBusinessId(businessId: Long): Flow<List<BasicField>> {
        return basicFieldQueries.findUserFieldsByBId(businessId).asFlow().list()
    }

    override fun findUserBasicFieldsByUId(uid: Long): List<Field> {
        return basicFieldValueQueries.selectUserBasicFieldsByUId(uid) { uid, fieldId, fieldValueId, fieldName, fieldValue, fieldType, description, validationRule ->
            Field(
                uid,
                fieldId,
                fieldValueId,
                fieldName!!,
                fieldType!!,
                description!!, validationRule!!, fieldValue,
                isBasic = true
            )
        }.executeAsList()
    }

    override fun findUserBizFieldsByUId(uid: Long): List<Field> {
        return bizFieldValueQueries.selectUserBizFieldsByUId(uid) { uid, fieldId, fieldValueId, fieldName, fieldValue, fieldType, description, validationRule ->
            Field(
                uid,
                fieldId, fieldValueId,
                fieldName!!, fieldType!!, description!!, validationRule!!, fieldValue
            )
        }.executeAsList()
    }

    override fun updateFieldValue(fieldValueId: Long, fieldValue: String) {
        basicFieldValueQueries.update(fieldValue, fieldValueId).ck()
    }

    override fun newFieldValue(
        uid: Long,
        fieldId: Long,
        fieldValue: String
    ): Long {
        val id = getSnowId()
        basicFieldValueQueries.insert(id, uid, fieldId, fieldValue)
        return id
    }
}