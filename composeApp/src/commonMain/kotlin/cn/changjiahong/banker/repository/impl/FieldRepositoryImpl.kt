package cn.changjiahong.banker.repository.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.FieldConfig
import cn.changjiahong.banker.RelFieldTplField
import cn.changjiahong.banker.SelectFieldsByUidAndBid
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.Field
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.repository.FieldRepository
import cn.changjiahong.banker.utils.getSnowId
import org.koin.core.annotation.Factory
import kotlin.Long

@Factory
class FieldRepositoryImpl(db: BankerDb) : FieldRepository {

    val fieldConfigQueries = db.fieldConfigQueries
    val relFieldTplFieldQueries = db.relFieldTplFieldQueries
    val fieldValueQueries = db.fieldValueQueries

    override suspend fun findFieldConfigsByBid(bId: Long): List<FieldConfig> {
        return fieldConfigQueries.selectBizFieldConfigsByBid(bId).executeAsList()
    }

    override suspend fun findFieldConfigsByBidWithGlobal(bId: Long): List<FieldConfig> {
        return fieldConfigQueries.selectFieldConfigsByBidWithGlobal(bId).executeAsList()

    }

    override fun newFieldConfig(
        bId: Long,
        fieldName: String,
        fieldType: String,
        alias: String,
        validationRule: String,
        forced: Boolean
    ): Long {
        val id = getSnowId()
        fieldConfigQueries.insert(
            id,
            bId,
            fieldName,
            fieldType,
            alias,
            validationRule,
            if (forced) 1 else 0
        ).ck()
        return id
    }

    override fun updateFieldConfigById(
        bId: Long,
        fieldName: String,
        fieldType: String,
        alias: String,
        validationRule: String,
        forced: Boolean,
        fieldId: Long
    ) {
        fieldConfigQueries.update(
            bId,
            fieldName,
            fieldType,
            alias,
            validationRule,
            if (forced) 1 else 0,
            fieldId
        )
    }

    override suspend fun findFieldConfigAndTplFieldMap(
        bId: Long,
        tid: Long
    ): List<RelFieldTplField> {
        return relFieldTplFieldQueries.selectFieldConfigAndTplFieldMap(bId, tid).executeAsList()
    }

    override fun newRelFieldTplField(
        bId: Long,
        tFieldId: Long,
        fieldId: Long?,
        fixed: Boolean,
        fixedValue: String
    ): Long {
        val id = getSnowId()
        relFieldTplFieldQueries.insert(
            id, fieldId, tFieldId, bId,
            if (fixed) 1 else 0, fixedValue
        ).ck()
        return id
    }

    override fun updateRelFieldTplField(
        tFieldId: Long,
        fieldId: Long?,
        fixed: Boolean,
        fixedValue: String,
        id: Long
    ) {
        relFieldTplFieldQueries.update(
            fieldId, tFieldId,
            if (fixed) 1 else 0, fixedValue, id
        ).ck()
    }


    override fun findFieldsByUidAndBid(uid: Long, bId: Long): List<Field> {
        return fieldValueQueries.selectFieldsByUidAndBid(bId, uid).executeAsList().map { f ->
            Field(
                f.uid,
                f.fieldId!!,
                fieldValueId = f.fieldValueId,
                fieldName = f.fieldName!!,
                fieldType = f.fieldType!!,
                alias = f.alias!!,
                validationRule = "",
                fieldValue = f.fieldValue,
                isBasic = f.global > 0
            )
        }
    }

    override fun findTplFieldVals(
        uid: Long,
        bId: Long,
        tid: Long
    ): List<FormFieldValue> {

        return fieldValueQueries.selectTplFieldVals(bId, uid, tid).executeAsList().map {
            FormFieldValue(
                it.fieldId!!,
                it.tFieldId!!,
                it.fieldValueId,
                it.fieldName!!,
                it.formFieldName!!,
                it.fieldType!!,
                it.formFieldType!!,
                it.fieldValue
            )
        }
    }

    override fun findFieldConfigInvolveTplFieldRel(bId: Long): List<FieldConfig> {
        return fieldConfigQueries.selectFieldConfigInvolveTplFieldRel(bId).executeAsList()
    }

    override fun newFieldValue(
        uid: Long,
        fieldId: Long,
        fieldValue: String
    ): Long {
        val id = getSnowId()
        fieldValueQueries.insert(id, uid, fieldId, fieldValue).ck()
        return id
    }

    override fun updateFieldValueById(fieldValueId: Long, fieldValue: String) {
        fieldValueQueries.update(fieldValue, fieldValueId).ck()

    }

    override suspend fun findFieldConfigsForTpl(
        bId: Long,
        tid: Long
    ): List<FieldConfig> {
        return fieldConfigQueries.selectFieldConfigsForTpl(bId, tid).executeAsList()
    }
}