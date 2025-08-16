package cn.changjiahong.banker.model

data class UserInfo(
    val id: Long,
    val fields: Map<String, Field>
)