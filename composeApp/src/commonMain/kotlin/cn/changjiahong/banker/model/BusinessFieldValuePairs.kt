package cn.changjiahong.banker.model

data class BusinessFieldValuePairs(
    val fieldId: Long,
    val fieldValueId: Long,
    val businessId: Long,
    val fieldName: String,
    val fieldType: String,
    val description: String,
    val fieldValue: String
)