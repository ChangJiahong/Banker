package cn.changjiahong.banker.repository

import cn.changjiahong.banker.Template
import cn.changjiahong.banker.TplField
import kotlinx.coroutines.flow.Flow

interface TemplateRepository {
    suspend fun findTemplatesByBusinessId(businessId: Long): Flow<List<Template>>
    suspend fun findTemplateFieldsById(templateId: Long): Flow<List<TplField>>

    suspend fun findTemplateFieldsById2(templateId: Long): List<TplField>
    suspend fun findAllDocTemps(): Flow<List<Template>>
    fun insertNewTemplateField(templateId: Long, fieldName: String, alias: String, fieldType: String): Long
    fun updateTemplateFieldById(fieldName: String, fieldType: String, alias: String, id: Long): Boolean
    suspend fun findTemplatesByFuzzyName(tempName: String): Flow<List<Template>>
    suspend fun insertNewTemplate(templateName: String, path: String, fileType: String): Long

}