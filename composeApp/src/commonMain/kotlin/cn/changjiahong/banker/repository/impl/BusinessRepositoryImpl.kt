package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BizField
import cn.changjiahong.banker.RelBizFieldTplField
import cn.changjiahong.banker.ExecuteError
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.FieldValuePair
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class BusinessRepositoryImpl(db: BankerDb) : BusinessRepository {
    private val businessQueries = db.businessQueries
    private val relBizTplQueries = db.relBizTplQueries
    private val bizFieldQueries = db.bizFieldQueries
    private val bizFieldValueQueries = db.bizFieldValueQueries
    private val relBizFieldTplFieldQueries = db.relBizFieldTplFieldQueries

    override suspend fun findBusinessTypes(): Flow<List<Business>> {
        return businessQueries.selectAll().asFlow().list()
    }

    /**
     * 获取业务属性，by BusinessId
     */
    override suspend fun findFieldsByBusinessId(businessId: Long): Flow<List<BizField>> {

        return bizFieldQueries.selectFieldsByBusinessId(businessId).asFlow().list()
    }

    override fun findFieldsById(businessId: Long): Flow<List<BizField>> {
        return bizFieldQueries.selectFieldsByBusinessId(businessId).asFlow().list()
    }

    override fun insertBusinessFieldValues(
        uid: Long,
        businessId: Long,
        fieldValues: Map<Long, String>
    ) {
        fieldValues.forEach { (fieldId, fieldValue) ->
            bizFieldValueQueries.insert(
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
            bizFieldQueries.selectBusinessFieldsByBusinessId(businessId).executeAsList()
        val businessFieldValues = bizFieldValueQueries.selectBusinessFieldValuesById(
            uid = userId,
            bId = businessId
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



    override fun insertRelTplFieldBizField(
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String
    ): Long {
        val id = getSnowId()
        relBizFieldTplFieldQueries.insert(
            id,
            businessFieldId,
            tempFieldId,
            if (fixed) 1 else 0,
            fixedValue
        )

        return id
    }

    override fun updateRelTplFieldBizField(
        id: Long,
        businessFieldId: Long?,
        tempFieldId: Long,
        fixed: Boolean,
        fixedValue: String
    ) {
        relBizFieldTplFieldQueries.update(
            businessFieldId,
            tempFieldId, if (fixed) 1 else 0,
            fixedValue, id
        )
    }

    override fun findFieldConfigMapByBidAndTid(
        bId: Long,
        tId: Long
    ): Flow<List<RelBizFieldTplField>> {
        return relBizFieldTplFieldQueries.selectByBidAndTid(bId, tId).asFlow().list()
    }

    override suspend fun insertTemplateIntoBusiness(businessId: Long, templateId: Long): Long {

        val re = relBizTplQueries.selectByBusinessAndTemplate(businessId, templateId)
            .executeAsOneOrNull()
        if (re != null) {
            throw ExecuteError("已添加该模版，请勿重复添加")
        }
        val id = getSnowId()
        relBizTplQueries.insert(id, businessId, templateId).ck()
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

    override fun insertBizField(
        fieldName: String,
        businessId: Long,
        fieldType: String,
        description: String,
        validationRule: String,
        fixed: Boolean,
        fixedValue: String
    ): Long {
        val id = getSnowId()
        bizFieldQueries.insert(
            id,
            businessId,
            fieldName,
            fieldType,
            description,
            validationRule,
            if (fixed) 1 else 0,
            fixedValue
        ).ck()
        return id
    }

    override fun updateBizField(
        id: Long,
        fieldName: String,
        businessId: Long,
        fieldType: String,
        description: String,
        validationRule: String,
        fixed: Boolean,
        fixedValue: String
    ) {
        bizFieldQueries.update(
            businessId,
            fieldName,
            fieldType,
            description,
            validationRule,
            if (fixed) 1 else 0,
            fixedValue,
            id
        ).ck()
    }

    override fun updateFieldValue(fieldValueId: Long, fieldValue: String) {
        bizFieldValueQueries.update(fieldValue,fieldValueId).ck()
    }

    override fun newFieldValue(
        uid: Long,
        businessId: Long,
        fieldId: Long,
        fieldValue: String
    ): Long {
        val id = getSnowId()
        bizFieldValueQueries.insert(id,uid,businessId,fieldId,fieldValue).ck()
        return id
    }
}