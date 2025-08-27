package cn.changjiahong.banker.model

data class FormField(val name: String, val type: String)


data class FormFieldValue(
    val tFieldId: Long,
    val tFieldName: String,
    val formFiledType: String,
    val fieldValue: String
)