package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.repository.SystemConfigRepository
import cn.changjiahong.banker.service.SystemConfigService
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class SystemConfigServiceImpl(val systemConfigRepository: SystemConfigRepository) :
    SystemConfigService {
    val PWD = "PWD"
    val FIRST_START = "FIRST_START"

    private fun getConfig(key: String, default: String = ""): String {
        return systemConfigRepository.findConfig(key)?.value_ ?: default
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

    override fun isFirstStart(): Flow<Boolean> = returnFlow {
        val fs = getConfig(FIRST_START, "true")
        fs == "true"
    }

    override fun unFirstStart(): Flow<NoData> = okFlow {
        val fs = getConfig(FIRST_START, "true")
        if (fs == "true") {
            saveConfig(FIRST_START, "false")
        }
    }
}