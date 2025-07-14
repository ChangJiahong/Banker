package cn.changjiahong.banker.repository.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.repository.EPayRepository
import org.koin.core.annotation.Factory

@Factory
class EPayRepositoryImpl(db: BankerDb) : EPayRepository {
    val ePayQueries = db.ePayQueries
    override fun insertEPay(
        uid: Long,
        bAddress: String,
        bScope: String,
        bankerNum: String
    ) {
        ePayQueries.insertEPay(uid, bAddress, bScope, bankerNum).ck()
    }
}