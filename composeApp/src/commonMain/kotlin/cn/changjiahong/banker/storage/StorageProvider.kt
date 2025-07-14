package cn.changjiahong.banker.storage

import cn.changjiahong.banker.storage.StorageProvider.homeDir

expect object StorageProvider {

    val homeDir: String
}

object Storage {
    val appDir: String = "$homeDir/Documents/Banker"
}