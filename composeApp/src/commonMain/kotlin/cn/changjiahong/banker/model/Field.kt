package cn.changjiahong.banker.model

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

data class FieldVal(
    val fieldId: Long,
    val fieldValueId: Long = -1,
    val fieldValue: String = "",
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
    val validationRule: String = "",
    val forced: Boolean = false,
    val isDelete: Boolean = false
)

data class FieldConfError(
    val fieldName: String = "",
    val fieldType: String = "",
    val alias: String = "",
    val width: String = "",
    val options: String = "",
    val validationRule: String = "",
)

data class RelFieldConfigTplField(
    val id: Long = -1,
    val tFieldId: Long? = null,
    val fieldId: Long? = null,
    val isFixed: Boolean = false,
    val fixedValue: String = "",
    val isDelete: Boolean = false
)

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
    val id: Long = -1,
    val fieldName: String = "",
    val alias: String = "",
    val fieldType: String = "",
    val isDelete: Boolean = false
)

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