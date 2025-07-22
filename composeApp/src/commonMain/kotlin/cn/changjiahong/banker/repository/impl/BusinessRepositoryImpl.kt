package cn.changjiahong.banker.repository.impl

import androidx.compose.runtime.mutableStateMapOf
import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFieldValue
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.model.BusinessFieldGroup
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.FieldValuePair
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Factory

@Factory
class BusinessRepositoryImpl(db: BankerDb) : BusinessRepository {
    private val businessQueries = db.businessQueries
    private val businessFieldQueries = db.businessFieldQueries
    private val businessFieldValueQueries = db.businessFieldValueQueries

    override suspend fun findBusinessTypes(): Flow<List<Business>> {
        return businessQueries.selectAll().asFlow().list()
    }

    override suspend fun findFieldsByBusinessId(): Flow<BusinessFields> {

        return businessFieldQueries.selectFieldsByBusinessId(1L).asFlow().list().map { fields ->

            val groupedFields = fields.groupBy { Pair(it.groupId, it.groupName ?: "") }

            val fieldGroups = groupedFields.map { (key, value) ->
                BusinessFieldGroup(key.first, key.second, value.map {
                    BusinessField(
                        it.fieldId,
                        it.businessId,
                        it.fieldName,
                        it.fieldType,
                        it.description,
                        it.validationRule,
                        it.groupId,
                        it.isFixed,
                        it.fixedValue,
                        it.created
                    )
                })
            }

            BusinessFields(fieldGroups)
        }
    }

    override fun insertBusinessFieldValues(
        uid: Long,
        businessId: Long,
        fieldValues: Map<Long, String>
    ) {
        fieldValues.forEach { (fieldId, fieldValue) ->
            businessFieldValueQueries.insertBusinessFieldValues(
                getSnowId(),
                uid,
                businessId,
                fieldId,
                fieldValue
            )
        }
    }

    override suspend fun findFieldMapById(
        businessId: Long,
        userId: Long
    ): Map<String, FieldValuePair> {
        val businessFields =
            businessFieldQueries.selectBusinessFieldsByBusinessId(businessId).executeAsList()
        val businessFieldValues = businessFieldValueQueries.selectBusinessFieldValuesById(
            uid = userId,
            businessId = businessId
        ).executeAsList()

        val businessFieldValueMap = mutableMapOf<String, FieldValuePair>()
        businessFieldValueMap.apply {
            businessFields.forEach { field ->
                if (field.isFixed > 0) {
                    put(
                        field.fieldName,
                        FieldValuePair(
                            field.id,
                            field.fieldName,
                            field.fieldType,
                            field.description,
                            field.fixedValue ?: ""
                        )
                    )
                } else {
                    val fv = businessFieldValues.find { it.fieldId == field.id }
                    if (fv != null) {
                        put(
                            field.fieldName,
                            FieldValuePair(
                                field.id,
                                field.fieldName,
                                field.fieldType,
                                field.description,
                                fv.fieldValue
                            )
                        )
                    }
                }
            }
        }

        return businessFieldValueMap
    }

    override suspend fun findBusinessFieldsMapById(
        businessId: Long,
        userId: Long
    ): Flow<Map<BusinessField, BusinessFieldValue>> {

        return businessFieldQueries.selectBusinessFieldsMapById(
            uid = userId,
            businessId = businessId
        ) { id, businessId, fieldName, fieldType, description, validationRule, groupId, isFixed, fixedValue, created,
            BFVId, uid, businessId_, fieldId, fieldValue, created_ ->

            val bf = BusinessField(
                id,
                businessId,
                fieldName,
                fieldType,
                description,
                validationRule,
                groupId,
                isFixed,
                fixedValue,
                created
            )
            val bfv: BusinessFieldValue
            if (BFVId == null || uid == null || businessId_ == null || fieldId == null || fieldValue == null || created_ == null) {
                if (bf.isFixed > 0 && bf.fixedValue != null) {
                    bfv = BusinessFieldValue(0, userId, businessId, 0, fixedValue!!, 0)
                } else {
                    throw ExecuteError("业务信息不完整，请完善信息")
                }
            } else {
                bfv = BusinessFieldValue(BFVId, uid, businessId_, fieldId, fieldValue, created_)
            }
            Pair(bf, bfv)
        }.asFlow().list().map {
            val map = mutableStateMapOf<BusinessField, BusinessFieldValue>()
            it.forEach { (key, value) ->
                map.put(key, value)
            }
            map
        }
    }
}