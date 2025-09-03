package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.repository.SystemConfigRepository
import cn.changjiahong.banker.service.SystemConfigService
import org.koin.core.annotation.Factory

@Factory
class SystemConfigServiceImpl(val systemConfigRepository: SystemConfigRepository): SystemConfigService {
    val PWD = "PWD"

    fun getConfig(key: String): String{
        systemConfigRepository.findConfig(PWD)?.value_

    }

    override fun getPwd(): String {
    }

    override fun savePwd(pwd: String) {
        TODO("Not yet implemented")
    }
}