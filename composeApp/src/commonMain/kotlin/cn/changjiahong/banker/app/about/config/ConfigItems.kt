package cn.changjiahong.banker.app.about.config

import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.app.RR.TEMPLATE_SETTING
import org.jetbrains.compose.resources.DrawableResource

val menusConfig = listOf(
    TempConfigItem,
    BusinessConfigItem,
    UserFiledConfigItem,
    PwdConfigItem
)

object TempConfigItem : MenusConfig {
    override val menuName: String
        get() = "模版文件维护"
    override val menuIcon: DrawableResource
        get() = Res.drawable.home
    override val screen: Screen
        get() = TEMPLATE_SETTING
}

object BusinessConfigItem : MenusConfig {
    override val menuName: String
        get() = "业务配置"
    override val menuIcon: DrawableResource
        get() = Res.drawable.home
    override val screen: Screen
        get() = RR.BUSINESS_SETTING
}

object UserFiledConfigItem : MenusConfig {
    override val menuName: String
        get() = "用户扩展字段设置"
    override val menuIcon: DrawableResource
        get() = Res.drawable.home
    override val screen: Screen?
        get() = null
}

object PwdConfigItem: MenusConfig{
    override val menuName: String
        get() = "密码设置"
    override val menuIcon: DrawableResource
        get() = Res.drawable.home
    override val screen: Screen?
        get() = null
}