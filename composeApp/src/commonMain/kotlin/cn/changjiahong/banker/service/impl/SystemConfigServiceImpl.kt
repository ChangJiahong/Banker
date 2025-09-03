package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.repository.SystemConfigRepository
import cn.changjiahong.banker.service.SystemConfigService
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import org.koin.core.annotation.Factory

@Factory
class SystemConfigServiceImpl(val systemConfigRepository: SystemConfigRepository) :
    SystemConfigService {
    val PWD = "PWD"

    private fun getConfig(key: String): String {
        return systemConfigRepository.findConfig(key)?.value_ ?: ""
    }

    private fun saveConfig(key: String, value: String) {
        val s = systemConfigRepository.findConfig(key)
        if (s == null) {
            systemConfigRepository.newConfig(key, value)
        } else {
            systemConfigRepository.updateConfig(key, value)
        }
    }

    override fun getPwd() = returnFlow {
        getConfig(PWD)
    }

    override fun savePwd(pwd: String) = okFlow {
        saveConfig(PWD, pwd)
    }
}