package cn.changjiahong.banker.app.about.config


import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.app.about.AboutScreenModel
import org.jetbrains.compose.resources.DrawableResource

interface MenusConfig {

    fun onClick(aboutScreenModel: AboutScreenModel){}

    val menuName: String

    val menuIcon: DrawableResource

    val containerColor: Long
        get() = 0xfff8f3fb

    val screen: Screen?


}