package cn.changjiahong.banker.repository.impl

import androidx.compose.runtime.mutableStateMapOf
import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFieldValue
import cn.changjiahong.banker.BusinessFiledTemplateFiledMap
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.BField
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
    private val businessTemplatesQueries = db.businessTemplatesQueries
    private val businessFieldQueries = db.businessFieldQueries
    private val businessFieldValueQueries = db.businessFieldValueQueries
    private val businessFiledTemplateFiledMapQueries = db.businessFiledTemplateFiledMapQueries

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
                        "",
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

    override fun findFieldsById(businessId: Long): Flow<List<BusinessField>> {
        return businessFieldQueries.selectBusinessFieldsByBusinessId(businessId).asFlow().list()
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


    override fun insertBusinessFields(insertData: List<BField>) {

    }

    override fun updateBusinessFields(updateData: List<BField>) {
        TODO("Not yet implemented")
    }

    override fun saveOrUpdateBusinessFields(fields: List<BusinessField>) {
        fields.forEach { (id, businessId, fieldName, toFormFieldName, fieldType, description, validationRule, groupId, isFixed, fixedValue, created) ->
            if (id < 0) {
                businessFieldQueries.insert(
                    getSnowId(),
                    businessId,
                    fieldName,
                    fieldType,
                    description,
                    validationRule,
                    groupId,
                    isFixed
                )
            } else {
                businessFieldQueries.update(
                    businessId,
                    fieldName,
                    fieldType,
                    description,
                    validationRule,
                    groupId,
                    isFixed,
                    id
                )
            }
        }
    }


    override fun insertBusinessTemplateFieldMap(
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String?
    ): Long {
        val id = getSnowId()
        businessFiledTemplateFiledMapQueries.insert(
            id,
            businessFieldId,
            tempFieldId,
            if (fixed) 1 else 0,
            fixedValue
        )

        return id
    }

    override fun updateBusinessTemplateFieldMap(
        id: Long,
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String?
    ) {
        businessFiledTemplateFiledMapQueries.update(
            businessFieldId,
            tempFieldId, if (fixed) 1 else 0,
            fixedValue, id
        )
    }

    override fun findFieldConfigMapByBidAndTid(
        bId: Long,
        tId: Long
    ): Flow<List<BusinessFiledTemplateFiledMap>> {
        return businessFiledTemplateFiledMapQueries.selectByBidAndTid(bId, tId).asFlow().list()
    }

    override suspend fun insertTemplateIntoBusiness(businessId: Long, templateId: Long): Long {

        val re = businessTemplatesQueries.selectByBusinessAndTemplate(businessId, templateId)
            .executeAsOneOrNull()
        if (re != null) {
            throw ExecuteError("已添加该模版，请勿重复添加")
        }
        val id = getSnowId()
        businessTemplatesQueries.insert(id, businessId, templateId).ck()
        return id
    }

    override suspend fun insertBusiness(name: String): Long {
        val re = businessQueries.selectByName(name).executeAsOneOrNull()
        if (re != null) {
            throw ExecuteError("已有该业务名称，请勿重复添加")
        }
        val id = getSnowId()
        businessQueries.insert(id, name).ck()
        return id
    }
}