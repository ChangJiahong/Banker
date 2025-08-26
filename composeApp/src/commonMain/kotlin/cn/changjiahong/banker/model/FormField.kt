package cn.changjiahong.banker.model

data class FormField(val name: String, val type: String)


data class FormFieldValue(
    val fieldId: Long,
    val tFieldId: Long,
    val fieldValueId: Long,
    val fieldName: String,
    val tFieldName: String,
    val filedType: String,
    val formFiledType: String,
    val fieldValue: String
)