package cn.changjiahong.banker

import android.os.Build

actual object Platform {
    actual val name: String = "Android ${Build.VERSION.SDK_INT}"

    actual val osPlatform: OSPlatform
        get() = OSPlatform.Android

    actual val deviceType: DeviceType
        get() {

            return DeviceType.Phone(osPlatform)
        }

    actual val isTablet: Boolean
        get() = deviceType is DeviceType.Pad

}

//actual fun getPlatform(): Platform = AndroidPlatform()