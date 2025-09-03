package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.FieldConfig
import cn.changjiahong.banker.RelFieldTplField
import cn.changjiahong.banker.SelectFieldConfigsForTpl
import cn.changjiahong.banker.model.FieldConf
import cn.changjiahong.banker.model.FieldVal
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.RelFieldConfigTplField
import cn.changjiahong.banker.repository.FieldRepository
import cn.changjiahong.banker.repository.UserRepository
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class FieldServiceImpl(
    val db: BankerDb,
    val userRepository: UserRepository,
    val fieldRepository: FieldRepository
) : FieldService {

    override fun getGlobalFieldConfigs(): Flow<List<FieldConfig>> = returnFlow {
        fieldRepository.findFieldConfigsByBid(-1)
    }


    override fun saveGlobalFieldConfigs(fieldConfigs: List<FieldConf>) = okFlow {
        db.transaction {
            fieldConfigs.forEach { fieldConfig ->
                if (fieldConfig.fieldId < 0 && !fieldConfig.isDelete) {
                    fieldRepository.newFieldConfig(
                        -1,
                        fieldConfig.fieldName,
                        fieldConfig.fieldType,
                        fieldConfig.alias,
                        fieldConfig.width,
                        fieldConfig.options,
                        fieldConfig.weight,
                        fieldConfig.validationRule,
                        fieldConfig.forced
                    )
                } else if (!fieldConfig.isDelete) {
                    fieldRepository.updateFieldConfigById(
                        -1,
                        fieldConfig.fieldName,
                        fieldConfig.fieldType,
                        fieldConfig.alias,
                        fieldConfig.width,
                        fieldConfig.options,
                        fieldConfig.weight,
                        fieldConfig.validationRule,
                        fieldConfig.forced,
                        fieldConfig.fieldId
                    )
                } else {
                    fieldRepository.deleteFieldConfigById(fieldConfig.fieldId)
                }
            }
        }
    }

    override fun getBizFieldConfigsByBid(bId: Long): Flow<List<FieldConfig>> = returnFlow {
        fieldRepository.findFieldConfigsByBid(bId)
    }

    override fun getFieldConfigs(bId: Long): Flow<List<FieldConfig>> = returnFlow {
        fieldRepository.findFieldConfigsByBidWithGlobal(bId)
    }

    override fun getFieldConfigsForTemplate(
        bId: Long,
        tid: Long
    ): Flow<List<SelectFieldConfigsForTpl>> = returnFlow {
        fieldRepository.findFieldConfigsForTpl(bId, tid)
    }

    override fun saveBizFieldConfigs(fieldConfigs: List<FieldConf>) = okFlow {
        db.transaction {
            fieldConfigs.forEach { fieldConfig ->
                if (fieldConfig.fieldId < 0 && !fieldConfig.isDelete) {
                    fieldRepository.newFieldConfig(
                        fieldConfig.bId,
                        fieldConfig.fieldName,
                        fieldConfig.fieldType,
                        fieldConfig.alias,
                        fieldConfig.width,
                        fieldConfig.options,
                        fieldConfig.weight,
                        fieldConfig.validationRule,
                        false
                    )
                } else if (!fieldConfig.isDelete) {
                    fieldRepository.updateFieldConfigById(
                        fieldConfig.bId,
                        fieldConfig.fieldName,
                        fieldConfig.fieldType,
                        fieldConfig.alias,
                        fieldConfig.width,
                        fieldConfig.options,
                        fieldConfig.weight,
                        fieldConfig.validationRule,
                        false,
                        fieldConfig.fieldId
                    )
                } else {
                    fieldRepository.deleteFieldConfigById(fieldConfig.fieldId)
                }
            }
        }
    }

    override fun getFieldConfigAndTplFieldMap(
        bId: Long,
        tid: Long
    ): Flow<List<RelFieldTplField>> = returnFlow {
        fieldRepository.findFieldConfigAndTplFieldMap(bId, tid)
    }

    override fun saveFieldConfigAndTplFieldMap(
        bId: Long,
        value: List<RelFieldConfigTplField>
    ): Flow<NoData> = okFlow {
        db.transaction {
            value.forEach { f ->
                if (f.id < 0 && !f.isDelete) {
                    fieldRepository.newRelFieldTplField(
                        bId,
                        f.tFieldId!!,
                        f.fieldId,
                        f.isFixed,
                        f.fixedValue
                    )
                } else if (!f.isDelete) {
                    fieldRepository.updateRelFieldTplField(
                        f.tFieldId!!,
                        f.fieldId,
                        f.isFixed,
                        f.fixedValue,
                        f.id
                    )
                } else {
                    fieldRepository.deleteRelFieldTplField(f.id)
                }
            }
        }
    }

    override fun getFieldConfigsForBiz(bId: Long) = returnFlow {
        // 查找涉及bid业务的属性配置，包括全局属性
        fieldRepository.findFieldConfigInvolveTplFieldRel(bId)
    }

    override fun saveFieldValues(
        uid: Long?,
        bId: Long,
        fieldVals: List<FieldVal>
    ): Flow<NoData> = okFlow {
        fieldRepository

        db.transaction {
            var uId = uid
            if (uId == null || uId < 0) {
                uId = userRepository.newUser()
            }
            fieldVals.forEach { fieldValue ->
                if (fieldValue.fieldValueId < 0) {
                    fieldRepository.newFieldValue(
                        uId,
                        fieldValue.fieldId,
                        fieldValue.fieldValue
                    )
                } else {
                    fieldRepository.updateFieldValueById(
                        fieldValue.fieldValueId,
                        fieldValue.fieldValue
                    )
                }
            }
        }
    }

    override fun getTplFieldVals(
        uid: Long,
        bId: Long,
        tid: Long
    ): Flow<List<FormFieldValue>> = returnFlow {
        fieldRepository.findTplFieldVals(uid, bId, tid)
    }
}