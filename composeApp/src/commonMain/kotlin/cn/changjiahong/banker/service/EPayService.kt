package cn.changjiahong.banker.service

import cn.changjiahong.banker.model.NoData
import kotlinx.coroutines.flow.Flow

interface EPayService {
    suspend fun saveEPay(
        username: String,
        idNum: String,
        phone: String,
        bAddress: String,
        bScope: String,
        bankerNum: String
    ): Flow<NoData>
}