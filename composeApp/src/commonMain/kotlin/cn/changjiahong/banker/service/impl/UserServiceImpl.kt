package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.RelBasicFieldTplField
import cn.changjiahong.banker.User
import cn.changjiahong.banker.model.BasicFieldConfig
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.RelTplFieldBasicFieldConfig
import cn.changjiahong.banker.model.TUExtendField
import cn.changjiahong.banker.model.UExtendField
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.model.UserField
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class UserServiceImpl(val db: BankerDb, val userRepository: UserRepository) : UserService {

    override suspend fun getUsers(): Flow<List<User>> {
        return userRepository.findAll()

    }

    override suspend fun getUserFieldsByBusinessId(id: Long): Flow<List<BasicField>> {
        return userRepository.findUserFieldsByBusinessId(id)
    }

    override suspend fun saveUFieldConfigs(value: List<BasicFieldConfig>): Flow<NoData> = flow {
        db.transaction {
            value.forEachIndexed { index, field ->
                if (field.id < 0) {
                    userRepository.insertUserExtendField(
                        field.fieldName,
                        field.description,
                        field.forced,
                        field.validationRule
                    )
                } else {
                    userRepository.updateUserExtendFieldById(
                        field.fieldName,
                        field.description,
                        field.forced,
                        field.validationRule,
                        field.id
                    )
                }
            }
        }
        emit(NoData)
    }

    override suspend fun getUserExtendFields(): Flow<List<BasicField>> {
        return userRepository.findUserExtendFields()
    }

    override suspend fun getUserFields(): Flow<List<UserField>> = flow {
        val userExtFields = userRepository.findUserExtFields()
        val userFields = mutableListOf<UserField>()
        userFields.apply {
            userExtFields.forEach {
                add(UserField(it.id, it.fieldName, it.description, it.validationRule ?: ""))
            }
        }


        emit(userFields)
    }

    override suspend fun saveRelTplFieldBasicFieldConfig(businessId: Long, value: List<RelTplFieldBasicFieldConfig>): Flow<NoData> = flow {
        db.transaction {
            value.forEachIndexed { index, field ->
                if (field.id < 0) {
                    userRepository.insertUserTempFieldMap(businessId,field.tempFieldId!!, field.userFieldId!!)
                } else {
                    userRepository.updateUserTempFieldMap(
                        field.id,
                        field.userFieldId!!,
                        field.tempFieldId!!
                    )
                }
            }
        }
        emit(NoData)
    }

    override suspend fun getFieldConfigMapByTIdAndBId(
        templateId: Long,
        businessId: Long
    ): Flow<List<RelBasicFieldTplField>> =
        flow {
            val res = userRepository.findFieldConfigMapByTid(templateId, businessId)
            emit(res)
        }
}