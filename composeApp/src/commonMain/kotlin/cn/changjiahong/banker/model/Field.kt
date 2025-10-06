package cn.changjiahong.banker.model

import androidx.compose.ui.text.font.FontWeight
import cn.changjiahong.banker.FieldOverrideBinding
import cn.changjiahong.banker.TplField
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.toBoolean
import kotlin.uuid.Uuid

/*
抽象出的属性类
 */
data class Field(
    val uid: Long = -1,
    val fieldId: Long,
    val fieldValueId: Long,
    val fieldName: String,
    val fieldType: String,
    val alias: String = "",
    val validationRule: String = "",
    val fieldValue: String,
    val isBasic: Boolean = false
)

data class Meta(
    val metaId: Long,
    val label: String,
    val metaType: String,
    val metaValueId: Long,
    val metaValue: String,
)

data class FieldVal(
    val fieldId: Long,
    val fieldValueId: Long = -1,
    val fieldValue: String = "",
)


data class MetaVal(
    val metaId: Long,
    val metaValueId: Long = -1,
    val metaValue: String = "",
)

data class Fields(
    val basicFields: List<Field>,
    val bizFields: List<Field>
)

data class FieldConf(
    val fieldId: Long = -1,
    val bId: Long,
    val fieldName: String = "",
    val fieldType: String = "TEXT",
    val alias: String = "",
    val width: Int = 20,
    val options: String = "",
    val weight: Int = 0,
    val validationRule: String = "",
    val forced: Boolean = false,
    val isDelete: Boolean = false
)

data class FieldConfError(
    val fieldName: String = "",
    val fieldType: String = "",
    val width: String = "",
    val options: String = "",
    val validationRule: String = "",
)

data class FieldOverrideBindingConfig(
    val id: Long = -1,
    val fieldId: Long = -1,
    val metaId: Long = -1,
    val bId: Long,
    val tId: Long,
    val isFixed: Boolean = false,
    val fixedValue: String = "",
    val isDelete: Boolean = false
) {

    constructor(binding: FieldOverrideBinding) : this(
        binding.id,
        binding.fieldId,
        binding.metaId ?: -1,
        binding.bId,
        binding.tId,
        binding.isFixed.toBoolean(),
        binding.fixedValue
    )

    data class Error(
        val fieldId: String = "",
        val metaId: String = "",
        val isFixed: String = "",
        val fixedValue: String = "",
    )
}

abstract class RelBizUIMetaItem(open val key: Int= getSnowId().toInt())

data class RelBizUIMetaTag( val tagName: String) : RelBizUIMetaItem()

data class RelBizUIMetaConfig(
    val id: Long,
    val bid: Long,
    val label: String,
    val metaId: Long,
    val weight: Long,
    val tag: String,
    val tagWeight: Long
) : RelBizUIMetaItem (){

}

data class RelFieldConfigTplFieldError(
    val tempFieldId: String = "",
    val businessFieldId: String = "",
    val isFixed: String = "",
    val fixedValue: String = "",
)


data class BasicFieldConfig(
    val fieldId: Long = -1,
    val fieldName: String = "",
    val fieldType: String = "",
    val description: String = "",
    val validationRule: String = "",
    val forced: Boolean = false
)

data class BasicFieldConfigError(
    val fieldName: String = "",
    val fieldType: String = "",
    val description: String = "",
    val validationRule: String = "",
    val forced: String = ""
)


data class TplFieldConfig(
    val fieldId: Long = -1,
    val formFieldName: String = "",
    val formFieldType: String = "",
    val metaId: Long = -1,
    val isFixed: Boolean = false,
    val fixedValue: String = "",
    val isDelete: Boolean = false
) {

    constructor(tplField: TplField) : this(
        fieldId = tplField.fieldId,
        formFieldName = tplField.formFieldName,
        formFieldType = tplField.formFieldType,
        metaId = tplField.metaId ?: -1,
        isFixed = tplField.isFixed.toBoolean(),
        fixedValue = tplField.fixedValue,
    )

    data class Error(
        val formFieldName: String = "",
        val formFieldType: String = "",
        val metaId: String = "",
        val fixedValue: String = ""
    )
}

data class TplFieldConfigError(
    val fieldName: String = "",
    val alias: String = "",
    val fieldType: String = ""
)

data class RelTplFieldBasicFieldConfig(
    val id: Long = -1,
    val tempFieldId: Long? = null,
    val userFieldId: Long? = null,
)

data class RelTplFieldBasicFieldConfigError(
    val tempFieldId: String = "",
    val userFieldId: String = "",
)