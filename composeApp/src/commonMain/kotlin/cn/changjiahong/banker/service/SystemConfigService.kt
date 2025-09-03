package cn.changjiahong.banker.service

interface SystemConfigService {

    fun getPwd(): String

    fun savePwd(pwd: String)

}