package cn.changjiahong.banker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TabHost(
    index: UShort,
    title: StringResource,
    icon: DrawableResource
): TabOptions {
    val title0 = stringResource(title)
    val icon0 = painterResource(icon)
    return remember {
        TabOptions(
            index = index,
            title = title0,
            icon = icon0
        )
    }
}