package cn.changjiahong.banker.platform

import androidx.compose.runtime.Composable
import cn.changjiahong.banker.storage.Storage
import io.github.vinceglb.filekit.PlatformFile
import java.awt.Desktop
import java.io.File

@Composable
actual fun SystemOpen(file: PlatformFile) {
    if (Desktop.isDesktopSupported()) {
        val desktop = Desktop.getDesktop()
        if (desktop.isSupported(Desktop.Action.OPEN)) {
            desktop.open(file.file)
        } else {
            println("当前系统不支持打开文件操作")
        }
    } else {
        println("Desktop 不支持")
    }
}