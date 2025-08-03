package cn.changjiahong.banker.app

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.app.about.AboutScreen
import cn.changjiahong.banker.app.about.settings.business.BusinessSettingScreen
import cn.changjiahong.banker.app.about.settings.business.tmp.BusinessFieldConfigScreen
import cn.changjiahong.banker.app.about.settings.business.tmp.BusinessTmpDetailScreen
import cn.changjiahong.banker.app.about.settings.business.tmp.FieldConfigScreen
import cn.changjiahong.banker.app.business_handle.BusinessHandlerScreen
import cn.changjiahong.banker.app.home.HomeScreen
import cn.changjiahong.banker.app.home.OptionsDirScreen
import cn.changjiahong.banker.app.main.MainScreen
import cn.changjiahong.banker.app.template.PreTemplateScreen
import cn.changjiahong.banker.app.template.TemplateScreen
import cn.changjiahong.banker.app.about.settings.template.TemplateSettingScreen
import cn.changjiahong.banker.app.xinef.EPayScreen

object RR {
    val MAIN = MainScreen

    val HOME = HomeScreen
    val ABOUT = AboutScreen


    val OPTIONS_DIR = OptionsDirScreen

    val E_PAY = EPayScreen

    val BUSINESS_HANDLER = { it: Business -> BusinessHandlerScreen(it) }

    val TEMPLATE_SETTING = TemplateSettingScreen
    val BUSINESS_SETTING = BusinessSettingScreen
    val BUSINESS_TMP_DETAIL = { b: Business -> BusinessTmpDetailScreen(b) }

    val TEMPLATE = { bId: Long, t: DocTemplate, uId: Long -> TemplateScreen(bId, t, uId) }
    val PRE_TEMPLATE = { t: DocTemplate -> PreTemplateScreen(t) }
    val FIELD_CONFIG = { b:Business,t: DocTemplate -> FieldConfigScreen(b,t) }
    val BUSINESS_FIELD_CONFIG = { b:Business -> BusinessFieldConfigScreen(b) }
}