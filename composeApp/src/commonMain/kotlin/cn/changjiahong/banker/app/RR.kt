package cn.changjiahong.banker.app

import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.about.AboutScreen
import cn.changjiahong.banker.app.about.settings.business.BusinessSettingScreen
import cn.changjiahong.banker.app.about.settings.business.tmp.BusinessFieldConfigScreen
import cn.changjiahong.banker.app.about.settings.business.tmp.BusinessTmpDetailScreen
import cn.changjiahong.banker.app.about.settings.business.tmp.FieldConfigScreen
import cn.changjiahong.banker.app.about.settings.template.TempFieldSettingScreen
import cn.changjiahong.banker.app.business_handle.BusinessHandlerScreen
import cn.changjiahong.banker.app.home.HomeScreen
import cn.changjiahong.banker.app.home.OptionsDirScreen
import cn.changjiahong.banker.app.main.MainScreen
import cn.changjiahong.banker.app.template.PreTemplateScreen
import cn.changjiahong.banker.app.template.TemplateScreen
import cn.changjiahong.banker.app.about.settings.template.TemplateSettingScreen
import cn.changjiahong.banker.app.about.settings.user.GlobalFieldSettingScreen
import cn.changjiahong.banker.model.Field

object RR {
    val MAIN = MainScreen

    val HOME = HomeScreen
    val ABOUT = AboutScreen


    val OPTIONS_DIR = OptionsDirScreen

    val BUSINESS_HANDLER = { it: Business -> BusinessHandlerScreen(it) }

    val TEMPLATE_SETTING = TemplateSettingScreen

    val TEMP_FIELD_SETTING ={ t: Template -> TempFieldSettingScreen(t)}

    val BUSINESS_SETTING = BusinessSettingScreen
    val BUSINESS_TMP_DETAIL = { b: Business -> BusinessTmpDetailScreen(b) }
    val GLOBAL_FIELD_SETTING = { GlobalFieldSettingScreen() }

    val TEMPLATE = {  uId: Long,bId: Long, t: Template,fields: List<Field> -> TemplateScreen(uId,bId, t, fields) }
    val PRE_TEMPLATE = { t: Template -> PreTemplateScreen(t) }
    val FIELD_CONFIG = { b:Business,t: Template -> FieldConfigScreen(b,t) }
    val BUSINESS_FIELD_CONFIG = { b:Business -> BusinessFieldConfigScreen(b) }
}