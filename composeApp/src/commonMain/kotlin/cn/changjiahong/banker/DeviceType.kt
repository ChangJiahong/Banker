package cn.changjiahong.banker

// shared/src/commonMain/kotlin/DeviceType.kt

sealed class DeviceType {
    abstract val os: OSPlatform
    abstract val formFactor: DeviceFormFactor

    data class Phone(val platform: OSPlatform) : DeviceType() {
        override val os = platform
        override val formFactor = DeviceFormFactor.Phone
    }

    data class Pad(val platform: OSPlatform) : DeviceType() {
        override val os = platform
        override val formFactor = DeviceFormFactor.Pad
    }

    data class Desktop(val platform: OSPlatform) : DeviceType() {
        override val os = platform
        override val formFactor = DeviceFormFactor.Desktop
    }
}

enum class OSPlatform {
    Android,
    IOS,
    Windows,
    MacOS,
    Linux,
    Unknown
}

enum class DeviceFormFactor {
    Phone,
    Pad,
    Desktop
}
