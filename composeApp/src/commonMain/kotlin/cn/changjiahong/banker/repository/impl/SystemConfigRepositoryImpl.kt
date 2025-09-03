package cn.changjiahong.banker.repository.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.SystemConfig
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.repository.SystemConfigRepository
import cn.changjiahong.banker.utils.getSnowId
import org.koin.core.annotation.Factory

@Factory
class SystemConfigRepositoryImpl(val db: BankerDb) : SystemConfigRepository {

    val systemConfigQueries = db.systemConfigQueries

    override fun newConfig(key: String, value: String) {
        systemConfigQueries.insert(key, value).ck()
    }

    override fun findConfig(key: String): SystemConfig? {
        return systemConfigQueries.select(key).executeAsOneOrNull()
    }

    override fun updateConfig(key: String, value: String) {
        systemConfigQueries.update(value,key).ck()
    }
}