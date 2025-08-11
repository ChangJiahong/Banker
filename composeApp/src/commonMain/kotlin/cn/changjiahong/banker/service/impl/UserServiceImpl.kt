package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.UserExtendField
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TUExtendField
import cn.changjiahong.banker.model.UExtendField
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.model.UserField
import cn.changjiahong.banker.model.are
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import org.koin.core.annotation.Factory

@Factory
class UserServiceImpl(val db: BankerDb, val userRepository: UserRepository) : UserService {

    override suspend fun getUsersByBR(br: BusinessRelated): Flow<List<UserDO>> {
        return userRepository.selectAll()
            .map {
                it.filter { user -> user.businessRelated are BusinessRelated.EPay }
            }
    }

    override suspend fun saveUFieldConfigs(value: List<UExtendField>): Flow<NoData> = flow {
        db.transaction {
            value.forEachIndexed { index, field ->
                if (field.id < 0) {
                    userRepository.insertUserExtendField(
                        field.fieldName,
                        field.description,
                        field.validationRule
                    )
                } else {
                    userRepository.updateUserExtendFieldById(
                        field.fieldName,
                        field.description,
                        field.validationRule,
                        field.id
                    )
                }
            }
        }
        emit(NoData)
    }

    override suspend fun getUserExtendFields(): Flow<List<UserExtendField>> {
        return userRepository.findUserExtendFields()
    }

    override suspend fun getUserFields(): Flow<List<UserField>> = flow {
        val userExtFields = userRepository.findUserExtFields()
        val userFields = mutableListOf<UserField>()
        userFields.apply {
            userExtFields.forEach {
                add(UserField(it.id, it.fieldName, it.description, it.validationRule ?: ""))
            }

            add(UserField(1, "user:name", "客户姓名"))
            add(UserField(2, "user:idNumber", "身份证号码"))
            add(UserField(3, "user:phone", "手机号码"))

        }


        emit(userFields)
    }

    override suspend fun saveUserTempFieldConfig(value: List<TUExtendField>): Flow<NoData> = flow {

    }
}