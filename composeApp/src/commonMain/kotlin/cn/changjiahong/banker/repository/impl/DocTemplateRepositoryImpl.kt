package cn.changjiahong.banker.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.TemplateField
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.repository.DocTemplateRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.list
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class DocTemplateRepositoryImpl(db: BankerDb) : DocTemplateRepository {

    val docTemplateQueries = db.docTemplateQueries
    val templateFieldQueries = db.templateFieldQueries

    override suspend fun findAllDocTemps(): Flow<List<DocTemplate>> {
        return docTemplateQueries.selectAllDocTemps().asFlow().list()
    }

    override suspend fun findTemplatesByBusinessId(businessId: Long): Flow<List<DocTemplate>> {
        return docTemplateQueries.selectTemplatesByBusinessId(businessId).asFlow().list()
    }

    override suspend fun findTemplateFieldsById(templateId: Long): Flow<List<TemplateField>> {

        return templateFieldQueries.selectTemplateFieldsById(templateId).asFlow().list()
    }

    override suspend fun findTemplateFieldsById2(templateId: Long): List<TemplateField> {
        return templateFieldQueries.selectTemplateFieldsById(templateId).executeAsList()
    }

    override fun insertNewTemplateField(
        templateId: Long,
        fieldName: String,
        fieldType: String
    ): Long {
        val id = getSnowId()
        templateFieldQueries.insert(id, templateId, fieldName, fieldType).ck()
        return id
    }

    override fun updateTemplateFieldById(
        fieldName: String,
        fieldType: String,
        id: Long
    ): Boolean {
        val res = templateFieldQueries.update(fieldName, fieldType, id)
        return res.value > 0
    }

    override suspend fun findTemplatesByFuzzyName(tempName: String): Flow<List<DocTemplate>> {
        return docTemplateQueries.selectTemplatesByFuzzyName(tempName).asFlow().list()
    }
}