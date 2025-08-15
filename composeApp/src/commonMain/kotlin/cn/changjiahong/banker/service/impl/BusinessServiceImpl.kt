package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BizField
import cn.changjiahong.banker.RelBizFieldTplField
import cn.changjiahong.banker.model.BField
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
    val db: BankerDb,
    val userRepository: UserRepository,
    val businessRepository: BusinessRepository
) : BusinessService {

    override suspend fun getBusinessList(): Flow<List<Business>> {
        return businessRepository.findBusinessTypes()
    }

    /**
     * 获取该业务的所有属性信息
     */
    override suspend fun getFieldsByBusinessId(businessId: Long): Flow<List<BizField>> {
        return businessRepository.findFieldsByBusinessId(businessId)
    }

    override suspend fun getFieldsById(
        businessId: Long,
        templateId: Long
    ): Flow<List<BizField>> {
        return businessRepository.findFieldsById(businessId)
    }

    override suspend fun getFieldsById(businessId: Long): Flow<List<BizField>> {
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
            BizField(
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

    override fun saveBusinessTemplateFieldConfig(data: List<TBField>): Flow<NoData> = flow {
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
    ): Flow<List<RelBizFieldTplField>> {
        return businessRepository.findFieldConfigMapByBidAndTid(bId, tId)
    }

    override fun addTemplate(
        businessId: Long,
        templateId: Long
    ): Flow<NoData> = flow {
        businessRepository.insertTemplateIntoBusiness(businessId, templateId)
        emit(NoData)
    }

    override fun addBusiness(name: String): Flow<NoData> = flow {
        businessRepository.insertBusiness(name)
        emit(NoData)
    }
}