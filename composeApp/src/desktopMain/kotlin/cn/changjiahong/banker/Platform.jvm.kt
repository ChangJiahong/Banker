package cn.changjiahong.banker


actual object Platform {
    private val osName = System.getProperty("os.name").lowercase()

    actual val name: String = "Java ${System.getProperty("java.version")}"

    actual val osPlatform: OSPlatform = when {
        osName.contains("win") -> OSPlatform.Windows
        osName.contains("mac") -> OSPlatform.MacOS
        osName.contains("nux") -> OSPlatform.Linux
        else -> OSPlatform.Unknown
    }

    actual val deviceType: DeviceType
        get() = DeviceType.Desktop(osPlatform)

    actual val isTablet: Boolean
        get() = false
}
