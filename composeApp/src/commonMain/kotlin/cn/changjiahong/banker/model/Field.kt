package cn.changjiahong.banker.model

/*
抽象出的属性类
 */
data class Field(
    val uid: Long = -1,
    val fieldName: String,
    val fieldType: String,
    val description: String = "",
    val validationRule: String = "",
    val fieldValue: String
)


data class BizFieldConfig(
    val id: Long = -1,
    val bId: Long,
    val fieldName: String = "",
    val fieldType: String = "TEXT",
    val description: String = "",
    val validationRule: String = "",
    val isFixed: Boolean = false,
    val fixedValue: String = "",
)

data class BizFieldConfigError(
    val fieldName: String = "",
    val fieldType: String = "",
    val description: String = "",
    val validationRule: String = "",
    val isFixed: String = "",
    val fixedValue: String = "",
)

data class RelTplFieldBizFieldConfig(
    val id: Long = -1,
    val tempFieldId: Long? = null,
    val businessFieldId: Long? = null,
    val isFixed: Boolean = false,
    val fixedValue: String = "",
)

data class RelTplFieldBizFieldConfigError(
    val tempFieldId: String = "",
    val businessFieldId: String = "",
    val isFixed: String = "",
    val fixedValue: String = "",
)


data class BasicFieldConfig(
    val id: Long = -1,
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
    val fieldType: String = ""
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