package cn.changjiahong.banker.app.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cn.changjiahong.banker.app.home.HomeScreen
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.ui.tooling.preview.Preview

object MainScreen : Screen {
    @Preview
    @Composable
    override fun Content() = MainView()
}

private val navigationTabs = arrayOf(HomeScreen)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    TabNavigator(RR.HOME) {
        Row(Modifier.background(Color(0xfffefbff))) {
            NavigationRail(
                modifier = Modifier,
                containerColor = Color(0xffeaeefb),
                header = { Box(Modifier.padding { paddingTop(50.dp) }) }
            ) {
                navigationTabs.forEach {
                    TabNavigationItem(it)
                }
            }
            CurrentTab()
        }
    }
}

@Composable
private fun TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationRailItem(
        tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            tab.options.icon?.let {
                Icon(
                    painter = it,
                    contentDescription = tab.options.title
                )
            }
        },
        label = { Text(tab.options.title) }
    )

}
