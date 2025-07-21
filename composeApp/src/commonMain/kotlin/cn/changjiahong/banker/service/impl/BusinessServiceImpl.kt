package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.BusinessService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class BusinessServiceImpl(
    val db: BankerDb, val userRepository: UserRepository,
    val businessRepository: BusinessRepository
) : BusinessService {

    override suspend fun getBusinessList(): Flow<List<Business>> {
        return businessRepository.findBusinessTypes()
    }

    override suspend fun getFieldsByBusinessId(businessId: Long): Flow<BusinessFields> {
        return businessRepository.findFieldsByBusinessId()
    }

    override suspend fun saveBusinessWithUserValue(
        username: String,
        idNumber: String,
        phone: String,
        businessId: Long,
        fieldValues: Map<Long, String>
    ) = flow {

        db.transaction {
            val uid = userRepository.insertUser(
                name = username,
                idNumber = idNumber,
                phone = phone,
                businessRelated = BusinessRelated.EPay
            )
            businessRepository.insertBusinessFieldValues(uid, businessId, fieldValues)
        }

        emit(NoData)
    }
}