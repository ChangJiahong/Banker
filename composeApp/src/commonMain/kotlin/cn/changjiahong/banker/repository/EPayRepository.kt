package cn.changjiahong.banker.repository

interface EPayRepository {
    fun insertEPay(uid: Long, bAddress: String, bScope: String, bankerNum: String)
}