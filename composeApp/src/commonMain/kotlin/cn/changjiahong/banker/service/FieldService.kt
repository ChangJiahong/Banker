package cn.changjiahong.banker.service

import cn.changjiahong.banker.FieldConfig
import cn.changjiahong.banker.RelFieldTplField
import cn.changjiahong.banker.SelectFieldConfigsForTpl
import cn.changjiahong.banker.model.FieldConf
import cn.changjiahong.banker.model.FieldVal
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.RelFieldConfigTplField
import kotlinx.coroutines.flow.Flow

interface FieldService {

    /**
     * 获取所有全局属性配置
     */
    fun getGlobalFieldConfigs(): Flow<List<FieldConfig>>

    /**
     * 保存全局属性配置
     */
    fun saveGlobalFieldConfigs(fieldConfigs: List<FieldConf>): Flow<NoData>

    /**
     * 获取该业务项下的业务属性配置
     */
    fun getBizFieldConfigsByBid(bId: Long): Flow<List<FieldConfig>>

    /**
     * 获取属性配置，包括bId业务项下的和全局的
     */
    fun getFieldConfigs(bId: Long): Flow<List<FieldConfig>>

    /**
     * 获取业务项下的某个模版的属性配置
     */
    fun getFieldConfigsForTemplate(bId: Long, tid: Long): Flow<List<SelectFieldConfigsForTpl>>

    /**
     * 保存业务项下的属性配置
     */
    fun saveBizFieldConfigs(fieldConfigs: List<FieldConf>): Flow<NoData>

    /**
     * 获取业务项（全局）属性和模版的属性映射关系
     */
    fun getFieldConfigAndTplFieldMap(bId: Long, tid: Long): Flow<List<RelFieldTplField>>

    /**
     * 保存业务项（全局）属性和模版的属性映射关系
     */
    fun saveFieldConfigAndTplFieldMap(
        bId: Long,
        value: List<RelFieldConfigTplField>
    ): Flow<NoData>

    /**
     * 获取涉及bid业务项的 全局属性配置
     */
    fun getFieldConfigsForBiz(bId: Long): Flow<List<FieldConfig>>

    /**
     * 保存属性的值
     */
    fun saveFieldValues(uid: Long?, bId: Long, fieldVals: List<FieldVal>): Flow<NoData>


    fun getTplFieldVals(uid: Long,bId: Long,tid: Long): Flow<List<FormFieldValue>>
}