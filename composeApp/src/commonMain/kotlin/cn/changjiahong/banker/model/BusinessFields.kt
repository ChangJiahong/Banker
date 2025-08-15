package cn.changjiahong.banker.model

import cn.changjiahong.banker.BizField

//data class BusinessFields(
//    val businessId: Long,
//    val businessName: String?,
//    val fields: List<BusinessField>
//)

data class BusinessFields(
    val fieldGroups: List<BusinessFieldGroup> = emptyList()
)

data class BusinessFieldGroup(
    val groupId: Long,
    val groupName: String,
    val fields: List<BizField>
)

data class BField(
    val id: Long = -1,
    val businessId: Long,
    val fieldName: String = "",
    val toFormFieldName: String = "",
    val fieldType: String = "TEXT",
    val description: String = "",
    val validationRule: String = "",
    val isFixed: Boolean = false,
    val fixedValue: String = "",
)

data class BFieldError(
    val fieldName: String = "",
    val description: String = "",
    val validationRule: String = "",
)


data class TBField(
    val id: Long = -1,
    val tempFieldId: Long? = null,
    val businessFieldId: Long? = null,
    val isFixed: Boolean = false,
    val fixedValue: String? = null,
)

data class TBFieldError(
    val tempFieldId: String = "",
    val businessFieldId: String = "",
    val fixedValue: String = "",
)