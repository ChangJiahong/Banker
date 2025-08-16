package cn.changjiahong.banker.model

data class UserField(
    val id: Long = -1,
    val fieldName: String = "",
    val description: String = "",
    val validationRule: String = "",
)