package cn.changjiahong.banker.app.about.config

import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.home
import banker.composeapp.generated.resources.bz
import banker.composeapp.generated.resources.globe
import banker.composeapp.generated.resources.security
import banker.composeapp.generated.resources.template
import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.app.RR.TEMPLATE_SETTING
import org.jetbrains.compose.resources.DrawableResource

val menusConfig = listOf(
    TempConfigItem,
    BusinessConfigItem,
    GlobalFiledConfigItem,
    PwdConfigItem
)

object TempConfigItem : MenusConfig {
    override val menuName: String
        get() = "模版文件维护"
    override val menuIcon: DrawableResource
        get() = Res.drawable.template
    override val screen: Screen
        get() = TEMPLATE_SETTING
}

object BusinessConfigItem : MenusConfig {
    override val menuName: String
        get() = "业务配置"
    override val menuIcon: DrawableResource
        get() = Res.drawable.bz
    override val screen: Screen
        get() = RR.BUSINESS_SETTING
}

object GlobalFiledConfigItem : MenusConfig {
    override val menuName: String
        get() = "全局字段配置"
    override val menuIcon: DrawableResource
        get() = Res.drawable.globe
    override val screen: Screen?
        get() = RR.GLOBAL_FIELD_SETTING()
}

object PwdConfigItem: MenusConfig{
    override val menuName: String
        get() = "密码设置"
    override val menuIcon: DrawableResource
        get() = Res.drawable.security
    override val screen: Screen?
        get() = null
}