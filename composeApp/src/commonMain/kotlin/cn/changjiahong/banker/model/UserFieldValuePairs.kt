package cn.changjiahong.banker.model

data class UserFieldValuePairs(
    val fieldId: Long,
    val uid: Long,
    val fieldName: String,
    val fieldType: String,
    val description: String,
    val fieldValue: String
) {
}