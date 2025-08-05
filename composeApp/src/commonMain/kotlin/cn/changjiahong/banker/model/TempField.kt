package cn.changjiahong.banker.model

data class TempField(
    val id: Long = -1,
    val fieldName: String? = null,
    val fieldType: String? = null
)

data class TempFieldError(
    val fieldName: String = "",
    val fieldType: String = ""
)