package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.TplField
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.repository.TemplateRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class TemplateRepositoryImpl(db: BankerDb) : TemplateRepository {

    val docTemplateQueries = db.templateQueries
    val templateFieldQueries = db.tplFieldQueries

    override suspend fun findAllDocTemps(): Flow<List<Template>> {
        return docTemplateQueries.selectAllDocTemps().asFlow().list()
    }

    override suspend fun findTemplatesByBusinessId(businessId: Long): Flow<List<Template>> {
        return docTemplateQueries.selectTemplatesByBusinessId(businessId).asFlow().list()
    }

    override suspend fun findTemplateFieldsById(templateId: Long): Flow<List<TplField>>  {

        return templateFieldQueries.selectTemplateFieldsById(templateId).asFlow().list()
    }

    override suspend fun findTemplateFieldsById2(templateId: Long): List<TplField> {
        return templateFieldQueries.selectTemplateFieldsById(templateId).executeAsList()
    }

    override fun insertNewTemplateField(
        templateId: Long,
        fieldName: String,
        alias: String,
        fieldType: String
    ): Long {
        val id = getSnowId()
        templateFieldQueries.insert(id, templateId, fieldName, fieldType,alias).ck()
        return id
    }

    override fun updateTemplateFieldById(
        fieldName: String,
        fieldType: String,
        alias: String,
        id: Long
    ): Boolean {
        val res = templateFieldQueries.update(fieldName, fieldType, alias,id)
        return res.value > 0
    }

    override suspend fun findTemplatesByFuzzyName(tempName: String): Flow<List<Template>> {
        return docTemplateQueries.selectTemplatesByFuzzyName(tempName).asFlow().list()
    }

    override suspend fun insertNewTemplate(
        templateName: String,
        path: String,
        fileType: String
    ): Long {
        val id = getSnowId()
        docTemplateQueries.insert(id, templateName, path, fileType).ck()
        return id
    }
}