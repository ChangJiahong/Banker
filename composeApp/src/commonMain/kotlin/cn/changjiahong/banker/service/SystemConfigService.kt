package cn.changjiahong.banker.service

import cn.changjiahong.banker.utils.NoData
import kotlinx.coroutines.flow.Flow

interface SystemConfigService {

    fun getPwd(): Flow<String>

    fun savePwd(pwd: String): Flow<NoData>

    fun isFirstStart(): Flow<Boolean>

    fun unFirstStart(): Flow<NoData>

}