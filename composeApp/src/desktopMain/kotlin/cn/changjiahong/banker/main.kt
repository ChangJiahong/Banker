package cn.changjiahong.banker

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.core.context.startKoin
import java.awt.Toolkit

fun main() = application {
// 获取屏幕尺寸（以像素为单位）
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val screenWidth = screenSize.width
    val screenHeight = screenSize.height

    // 设置窗口大小为屏幕一半（转换为 dp）
    val windowWidthDp = (screenWidth /2).dp
    val windowHeightDp = (screenHeight*0.8).dp

    val windowState = rememberWindowState(
        width = windowWidthDp,
        height = windowHeightDp,
        // 注意 initialPlacement 要设置为 Center
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.Center)
    )

    startKoin {
        modules(appModules)
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "Banker",
        state = windowState
    ) {
        App()
    }
}