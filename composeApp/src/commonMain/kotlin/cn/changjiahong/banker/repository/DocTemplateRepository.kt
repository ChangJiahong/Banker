package cn.changjiahong.banker.repository

import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.TemplateField
import kotlinx.coroutines.flow.Flow

interface DocTemplateRepository {
    suspend fun findTemplatesByBusinessId(businessId: Long): Flow<List<DocTemplate>>
    suspend fun findTemplateFieldsById(templateId: Long): Flow<List<TemplateField>>

    suspend fun findTemplateFieldsById2(templateId: Long): List<TemplateField>
    suspend fun findAllDocTemps(): Flow<List<DocTemplate>>
    fun insertNewTemplateField(templateId: Long, fieldName: String, fieldType: String): Long
    fun updateTemplateFieldById(fieldName: String, fieldType: String, id: Long): Boolean
    suspend fun findTemplatesByFuzzyName(tempName: String): Flow<List<DocTemplate>>

}