package cn.changjiahong.banker.repository

import cn.changjiahong.banker.SystemConfig

interface SystemConfigRepository {
    fun newConfig(key: String, value: String)

    fun findConfig(key: String): SystemConfig?

    fun updateConfig(key: String, value: String)
}