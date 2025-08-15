package cn.changjiahong.banker.repository

import cn.changjiahong.banker.User
import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.BasicFieldValue
import cn.changjiahong.banker.RelBasicFieldTplField
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.FieldValuePair
import cn.changjiahong.banker.model.UserDO
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun selectAll(): Flow<List<UserDO>>

    fun selectById(id: Long): User

    fun insertUser(
        name: String,
        idNumber: String,
        phone: String,
        businessRelated: BusinessRelated
    ): Long


    suspend fun findUserById(userId: Long): Flow<User>


    suspend fun findUserFieldsMapById(userId: Long): Flow<Map<BasicField, BasicFieldValue>>

    suspend fun findFieldMapById(userId: Long): Map<String, FieldValuePair>
    fun insertUserExtendField(
        fieldName: String,
        description: String,
        forced: Boolean,
        validationRule: String
    ): Long

    fun updateUserExtendFieldById(
        fieldName: String,
        description: String,
        forced: Boolean,
        validationRule: String,
        id: Long
    )

    suspend fun findUserExtendFields(): Flow<List<BasicField>>
    fun findUserExtFields(): List<BasicField>
    fun insertUserTempFieldMap(businessId: Long,tempFieldId: Long, userFieldId: Long): Long
    fun updateUserTempFieldMap(id: Long, userFieldId: Long, tempFieldId: Long)
    suspend fun findFieldConfigMapByTid(templateId: Long,businessId: Long): List<RelBasicFieldTplField>
    suspend fun findUserFieldsByBusinessId(businessId: Long): Flow<List<BasicField>>

}