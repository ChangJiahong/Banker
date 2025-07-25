package cn.changjiahong.banker.app.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import cn.changjiahong.banker.uieffect.GoEffect
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
        Text("版本 1.0.0-beta", fontSize = 38.sp)

        Card(Modifier.width(300.dp).padding{paddingTop(25.dp)
        paddingBottom(5.dp)}){
            SettingsMenuLink(
                title = { Text(text = "模版文件维护") },
                enabled = true,
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = ""
                    )
                },
                colors = SettingsTileDefaults.colors(containerColor = Color(0xfff8f3fb)),
                onClick = {
                    AboutUiEvent.GoTemplateSetting.sendTo(aboutScreenModel)
                },
            )
        }

        Card(Modifier.width(300.dp).padding{paddingVertical(5.dp)}) {
            SettingsMenuLink(
                title = { Text(text = "业务配置") },
                enabled = true,
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = ""
                    )
                },
                colors = SettingsTileDefaults.colors(containerColor = Color(0xfff8f3fb)),
                onClick = {
                    AboutUiEvent.GoBusinessSetting.sendTo(aboutScreenModel)
                },
            )
        }

        Card(Modifier.width(300.dp).padding{paddingVertical(5.dp)}) {
            SettingsMenuLink(
                title = { Text(text = "密码设置") },
                enabled = true,
                icon = {
                    Icon(
                        painter = painterResource(Res.drawable.home),
                        contentDescription = ""
                    )
                },
                colors = SettingsTileDefaults.colors(containerColor = Color(0xfff8f3fb)),
                onClick = { },
            )
        }

    }
}