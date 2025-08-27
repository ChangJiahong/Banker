package cn.changjiahong.banker.platform

import cn.changjiahong.banker.model.BError
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.utils.okFlow
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import java.awt.Desktop

actual object SystemPrinter {
    actual fun print(file: PlatformFile): Flow<NoData> = okFlow{
        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.APP_PRINT_FILE)) {
                desktop.print(file.file)
            } else {
                throw BError.Fail("打印操作不支持")
            }
        } else {
            throw BError.Fail("Desktop 不支持")
        }
    }
}