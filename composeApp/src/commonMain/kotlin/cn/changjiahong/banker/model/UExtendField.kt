package cn.changjiahong.banker.model

data class UExtendField(
    val id: Long = -1,
    val fieldName: String = "",
    val description: String = "",
    val validationRule: String = "",
)
data class UExtendFieldError(
    val fieldName: String = "",
    val description: String = "",
    val validationRule: String = "",
)

data class TUExtendField(
    val id: Long = -1,
    val tempFieldId: Long? = null,
    val userFieldId: Long? = null,
)

data class TUExtendFieldError(
    val tempFieldId: String="",
    val userFieldId: String="",
)

data class UserField(
    val id: Long = -1,
    val fieldName: String = "",
    val description: String = "",
    val validationRule: String = "",
)