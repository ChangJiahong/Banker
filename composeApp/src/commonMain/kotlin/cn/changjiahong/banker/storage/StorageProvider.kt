package cn.changjiahong.banker.storage

import cn.changjiahong.banker.storage.StorageProvider.homeDir

expect object StorageProvider {

    val homeDir: String

    fun getTempFilePath(fileName: String): String
}

object Storage {
    val appDir: String = "$homeDir/Documents/Banker"
    val tempDir: String = "$appDir/temp"

    fun getTempFilePath(fileName: String) = StorageProvider.getTempFilePath(fileName)
}