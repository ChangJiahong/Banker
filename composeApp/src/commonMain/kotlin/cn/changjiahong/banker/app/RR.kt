package cn.changjiahong.banker.app

import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.business_handle.BusinessHandlerScreen
import cn.changjiahong.banker.app.home.HomeScreen
import cn.changjiahong.banker.app.home.OptionsDirScreen
import cn.changjiahong.banker.app.main.MainScreen
import cn.changjiahong.banker.app.pdf_template.PdfTemplateScreen
import cn.changjiahong.banker.app.xinef.EPayScreen

object RR {
    val MAIN = MainScreen

    val HOME = HomeScreen

    val OPTIONS_DIR = OptionsDirScreen

    val E_PAY = EPayScreen

    val BUSINESS_HANDLER = { it: Business -> BusinessHandlerScreen(it) }

    val PDF_TEMPLATE = PdfTemplateScreen
}