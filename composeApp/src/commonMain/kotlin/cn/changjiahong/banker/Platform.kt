package cn.changjiahong.banker

expect object Platform {
    val name: String
    val osPlatform: OSPlatform

    val deviceType: DeviceType

    val isTablet: Boolean

}

