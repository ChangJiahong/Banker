package cn.changjiahong.banker.service

import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.model.TemplateFillerItem
import kotlinx.coroutines.flow.Flow

interface TemplateService {

    suspend fun getAllDocTemps(): Flow<List<DocTemplate>>

    suspend fun getDocTempsByBusinessId(businessId: Long): Flow<List<DocTemplate>>

    suspend fun getTemplateFillerData(businessId: Long, templateId: Long, userId: Long): Flow<List<TemplateFillerItem>>

    suspend fun checkTemplateFillerDataIsComplete(businessId: Long, templateId: Long, userId: Long): Flow<Boolean>
}