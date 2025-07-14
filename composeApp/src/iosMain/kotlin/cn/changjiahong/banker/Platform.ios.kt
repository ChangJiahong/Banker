package cn.changjiahong.banker

import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.UIUserInterfaceIdiomPhone

actual object Platform {
     actual val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

     private val userInterfaceIdiom = UIDevice.currentDevice.userInterfaceIdiom

     actual val osPlatform: OSPlatform
          get() = OSPlatform.IOS

     actual val deviceType: DeviceType
          get() = when (userInterfaceIdiom) {
               UIUserInterfaceIdiomPad -> DeviceType.Pad(osPlatform)
               UIUserInterfaceIdiomPhone -> DeviceType.Phone(osPlatform)
               else -> DeviceType.Phone(osPlatform) // 默认视为手机
          }

     actual val isTablet: Boolean
          get() = deviceType is DeviceType.Pad

}
