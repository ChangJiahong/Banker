package cn.changjiahong.banker.model

data class FieldValuePair(
    val fieldId: Long,
    val fieldName: String,
    val fieldType: String,
    val description: String,
    val fieldValue: String
)
