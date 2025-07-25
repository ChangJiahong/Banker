package cn.changjiahong.banker.app.about

import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.uieffect.GoEffect
import org.koin.core.annotation.Factory

sealed interface AboutUiEvent: UiEvent{
    object GoTemplateSetting : AboutUiEvent

    object GoBusinessSetting: AboutUiEvent
}

@Factory
class AboutScreenModel: MviScreenModel() {
    override fun handleEvent(event: UiEvent) {
        when(event){
            is AboutUiEvent.GoTemplateSetting -> GoEffect(RR.TEMPLATE_SETTING).trigger()

            is AboutUiEvent.GoBusinessSetting -> GoEffect(RR.BUSINESS_SETTING).trigger()
        }
    }
}