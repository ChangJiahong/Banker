package cn.changjiahong.banker.app.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.app_name
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cn.changjiahong.banker.TabHost
import cn.changjiahong.banker.app.about.config.menusConfig
import cn.changjiahong.banker.uieffect.GoEvent
import cn.changjiahong.banker.utils.padding
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.alorma.compose.settings.ui.base.internal.SettingsTileDefaults
import org.jetbrains.compose.resources.painterResource

object AboutScreen : Tab {
    override val options: TabOptions
        @Composable
        get() = TabHost(1u, Res.string.app_name, Res.drawable.home)

    @Composable
    override fun Content() = AboutView()
}

@Composable
fun AboutScreen.AboutView(aboutScreenModel: AboutScreenModel = koinScreenModel()) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xfffefbff)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "版本 1.0.0-beta",
            fontSize = 38.sp,
            modifier = Modifier.padding { paddingBottom(20.dp) })

        menusConfig.forEachIndexed { index, item ->

            Card(Modifier.width(300.dp).padding { paddingVertical(5.dp) }) {
                SettingsMenuLink(
                    title = { Text(text = item.menuName) },
                    enabled = true,
                    icon = {
                        Icon(
                            painter = painterResource(item.menuIcon),
                            contentDescription = ""
                        )
                    },
                    colors = SettingsTileDefaults.colors(containerColor = Color(item.containerColor)),
                    onClick = {
                        if (item.screen!=null) {
                            GoEvent(item.screen!!).sendTo(aboutScreenModel)
                        }else{
                            item.onClick(aboutScreenModel)
                        }
                    },
                )
            }
        }

    }
}