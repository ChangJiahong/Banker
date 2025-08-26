package cn.changjiahong.banker.repository

import cn.changjiahong.banker.FieldConfig
import cn.changjiahong.banker.RelFieldTplField
import cn.changjiahong.banker.model.Field
import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import org.koin.core.scope.ScopeID

interface FieldRepository {

    /**
     * 获取属性配置通过业务id
     */
    suspend fun findFieldConfigsByBid(bId: Long): List<FieldConfig>

    /**
     * 获取属性配置通过业务id，自动包含全局属性，即bid=-1
     */
    suspend fun findFieldConfigsByBidWithGlobal(bId: Long): List<FieldConfig>

    /**
     * 新建属性配置
     * @param forced 是否是必输项
     */
    fun newFieldConfig(
        bId: Long,
        fieldName: String,
        fieldType: String,
        alias: String,
        validationRule: String,
        forced: Boolean
    ): Long

    /**
     * 更新属性配置
     */
    fun updateFieldConfigById(
        bId: Long,
        fieldName: String,
        fieldType: String,
        alias: String,
        validationRule: String,
        forced: Boolean,
        fieldId: Long
    )

    /**
     * 获取业务项（全局）属性和模版的属性映射关系
     */
    suspend fun findFieldConfigAndTplFieldMap(bId: Long, tid: Long): List<RelFieldTplField>

    /**
     * 新建属性和模版的属性映射关系
     */
    fun newRelFieldTplField(
        bId: Long,
        tFieldId: Long,
        fieldId: Long?,
        fixed: Boolean,
        fixedValue: String
    ): Long

    /**
     * 更新属性和模版的属性映射关系
     */
    fun updateRelFieldTplField(
        tFieldId: Long,
        fieldId: Long?,
        fixed: Boolean,
        fixedValue: String,
        id: Long
    )

    /**
     * 获取uid在bid业务项下的属性名称和值集合
     */
    fun findFieldsByUidAndBid(uid: Long,bId: Long): List<Field>

    /**
     * 获取uid 模版属性，bid业务项下的-值的映射
     */
    fun findTplFieldVals(uid: Long,bId: Long,tid: Long): List<FormFieldValue>

    /**
     * 查找涉及bid业务的属性配置，包括全局属性
     */
    fun findFieldConfigInvolveTplFieldRel(bId: Long): List<FieldConfig>

    fun newFieldValue(uid: Long, fieldId: Long, fieldValue: String): Long

    fun updateFieldValueById(fieldValueId: Long, fieldValue: String)

    /**
     * 获取业务项下的某个模版的属性配置
     */
    suspend fun findFieldConfigsForTpl(bId: Long, tid: Long): List<FieldConfig>
}