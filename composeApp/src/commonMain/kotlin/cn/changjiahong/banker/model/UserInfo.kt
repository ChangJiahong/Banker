package cn.changjiahong.banker.model

data class UserInfo(
    val uid: Long = -1,
    val metas: List<Meta> = emptyList(),
    val fields: Map<String, Field> = emptyMap()
)