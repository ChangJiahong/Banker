package cn.changjiahong.banker.storage

import cn.changjiahong.banker.OSPlatform
import cn.changjiahong.banker.Platform
import java.io.File
import java.net.URI

actual object StorageProvider {
    actual val homeDir: String
        get() = when (Platform.osPlatform) {
            OSPlatform.MacOS, OSPlatform.Linux -> System.getProperty("user.home")
            OSPlatform.Windows -> System.getenv("USERPROFILE")
            else -> "/"
        }

    actual fun getTempFilePath(fileName: String): String {
        val tempDir = File(Storage.tempDir)
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return "${Storage.tempDir}/$fileName"
    }

}