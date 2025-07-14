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

}