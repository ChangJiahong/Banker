package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFiledTemplateFiledMap
import cn.changjiahong.banker.model.BField
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TBField
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

    override suspend fun getFieldsById(
        businessId: Long,
        templateId: Long
    ): Flow<List<BusinessField>> {
        return businessRepository.findFieldsById(businessId)
    }

    override suspend fun getFieldsById(businessId: Long): Flow<List<BusinessField>> {
        return businessRepository.findFieldsById(businessId)
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

    override suspend fun saveBFieldConfigs(value: List<BField>): Flow<NoData> = flow {
        val data = value.map {
            BusinessField(
                it.id, it.businessId, it.fieldName,
                "", it.fieldType, it.description, it.validationRule, 0, 0, "",
                0
            )
        }
        db.transaction {
            businessRepository.saveOrUpdateBusinessFields(data)
        }
        emit(NoData)
    }

    override fun saveBusinessTemplateFieldConfig(data: List<TBField>): Flow<NoData> =flow {
        db.transaction {
            data.forEach { tBField ->
                if (tBField.id < 0) {
                    businessRepository.insertBusinessTemplateFieldMap(
                        tBField.businessFieldId,
                        tBField.tempFieldId!!,
                        tBField.isFixed,
                        tBField.fixedValue
                    )
                } else {
                    businessRepository.updateBusinessTemplateFieldMap(
                        tBField.id,
                        tBField.businessFieldId,
                        tBField.tempFieldId!!,
                        tBField.isFixed,
                        tBField.fixedValue
                    )
                }
            }
        }

        emit(NoData)
    }

    override fun getFieldConfigMapByBidAndTid(
        bId: Long,
        tId: Long
    ): Flow<List<BusinessFiledTemplateFiledMap>> {
        return businessRepository.findFieldConfigMapByBidAndTid(bId,tId)
    }
}