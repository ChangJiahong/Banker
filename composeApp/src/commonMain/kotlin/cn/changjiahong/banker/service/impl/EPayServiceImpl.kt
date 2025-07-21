package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.repository.EPayRepository
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.EPayService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory
import kotlin.String

@Factory
class EPayServiceImpl(
    val db: BankerDb,
    val ePayRepository: EPayRepository,
    val userRepository: UserRepository
) : EPayService {
    override suspend fun saveEPay(
        username: String,
        idNum: String,
        phone: String,
        bAddress: String,
        bScope: String,
        bankerNum: String
    ): Flow<NoData> = flow {
        db.transaction {
            val uid = userRepository.insertUser(
                name = username,
                idNumber = idNum,
                phone = phone,
                businessRelated = BusinessRelated.EPay
            )
//            val uid = userRepository.getLastUserId()
            ePayRepository.insertEPay(uid, bAddress, bScope, bankerNum)
        }
        emit(NoData)
    }
}
