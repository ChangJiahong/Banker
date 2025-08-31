package cn.changjiahong.banker.app.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.app_name
import banker.composeapp.generated.resources.arrow_back
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cn.changjiahong.banker.TabHost
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.uieffect.DIREffectRegister
import cn.changjiahong.banker.uieffect.NavigatorEffectRegister
import org.jetbrains.compose.resources.painterResource


object HomeScreen : Tab {
    @Composable
    override fun Content() = HomeView()

    override val options: TabOptions
        @Composable
        get() = TabHost(0u, Res.string.home, Res.drawable.home)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView() {
    Navigator(RR.OPTIONS_DIR) { navigator ->
        DIREffectRegister(navigator)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row {
                            navigator.items.forEachIndexed { index, sc ->
                                if (sc !is DirScreen) {
                                    return@forEachIndexed
                                }
                                BreadcrumbTextItem(index, sc.dirName) {
                                    navigator.popUntil {
                                        it.key == sc.key
                                    }
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            navigator.pop()
                        }, modifier = Modifier) {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_back),
                                contentDescription = "arrow back"
                            )
                        }
                    },
                    expandedHeight = 40.dp,
                    colors = TopAppBarDefaults.topAppBarColors(Color(0xfff2f3fc))
                )

            }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                CurrentScreen()
            }
        }
    }
}


@Composable
fun BreadcrumbTextItem(index: Int, text: String, onclick: () -> Unit) {
    Text(
        text,
        modifier = Modifier
            .offset(x = (if (index != 0) -5 else 0).dp, y = 0.dp)
            .clip(ParallelogramShape())
            .background(Color(if (index % 2 == 1) 0xffd9e2ff else 0xffe1e2ec))
            .padding(10.dp, 0.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = ripple(
                    bounded = false,
                    radius = 100.dp
                ),
                onClick = onclick
            )
    )
}


class ParallelogramShape(
    private val skewX: Float = 10f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(skewX, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width - skewX, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}
