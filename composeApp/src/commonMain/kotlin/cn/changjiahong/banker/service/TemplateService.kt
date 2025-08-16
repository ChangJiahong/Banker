package cn.changjiahong.banker.service

import cn.changjiahong.banker.Template
import cn.changjiahong.banker.TplField
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.model.TplFieldConfig
import kotlinx.coroutines.flow.Flow

interface TemplateService {

    suspend fun getAllDocTemps(): Flow<List<Template>>

    suspend fun getDocTempsByBusinessId(businessId: Long): Flow<List<Template>>

    suspend fun getTemplateFillerData(
        businessId: Long,
        templateId: Long,
        userId: Long
    ): Flow<List<TemplateFillerItem>>

    suspend fun checkTemplateFillerDataIsComplete(
        businessId: Long,
        templateId: Long,
        userId: Long
    ): Flow<Boolean>

    suspend fun getFieldsByTemplateId(id: Long): Flow<List<TplField>>
    fun saveOrUpdateFieldsConfig(templateId: Long,fieldConfigs: List<TplFieldConfig>): Flow<NoData>
    suspend fun fuzzySearchByTempName(tempName: String): Flow<List<Template>>
    suspend fun addNewTemplate(path: String, templateName: String, fileType: String): Flow<NoData>

}