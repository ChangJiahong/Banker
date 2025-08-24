package cn.changjiahong.banker.model

data class UserInfo(
    val id: Long = -1,
    val fields: Map<String, Field> = emptyMap()
)