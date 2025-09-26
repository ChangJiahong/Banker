package cn.changjiahong.banker.repository

import cn.changjiahong.banker.Template
import cn.changjiahong.banker.TplField
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {

    suspend fun findTemplatesByBusinessId(businessId: Long): List<Template>

    fun findTemplateFieldsById(templateId: Long): List<TplField>

    suspend fun findTemplateFieldsById2(templateId: Long): List<TplField>
    suspend fun findAllDocTemps(): Flow<List<Template>>


    fun findBizInvolvedUIMeta(bid: Long): List<Long>

    fun insertNewTemplateField(
        templateId: Long,
        formFieldName: String,
        formFieldType: String,
        metaId: Long?,
        isFixed: Boolean,
        fixedValue: String
    ): Long

    fun updateTemplateFieldById(
        fieldName: String,
        fieldType: String,
        metaId: Long?,
        isFixed: Boolean,
        fixedValue: String,
        id: Long
    ): Boolean

    fun deleteTemplateFieldById(id: Long)

    suspend fun findTemplatesByFuzzyName(tempName: String): Flow<List<Template>>
    suspend fun insertNewTemplate(templateName: String, path: String, fileType: String): Long
    suspend fun deleteTemplate(tid: Long)

}