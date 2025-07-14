package cn.changjiahong.banker.model

import cn.changjiahong.banker.BusinessField

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
    val fields: List<BusinessField>
)
