package cn.changjiahong.banker.service

import cn.changjiahong.banker.model.NoData
import kotlinx.coroutines.flow.Flow

interface SystemConfigService {

    fun getPwd(): Flow<String>

    fun savePwd(pwd: String): Flow<NoData>

}