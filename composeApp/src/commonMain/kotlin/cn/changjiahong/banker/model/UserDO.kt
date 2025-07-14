package cn.changjiahong.banker.model

import kotlinx.datetime.Instant

public data class UserDO(
    public val id: Long,
    public val name: String,
    public val idNumber: String,
    public val phone: String?,
    public val address: String?,
    public val businessRelated: BusinessRelated,
    public val created: Instant
)