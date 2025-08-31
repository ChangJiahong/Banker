package cn.changjiahong.banker.app.about.settings

import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent

sealed interface ConfigUiEvent : UiEvent {
    object Add : ConfigUiEvent
    class Delete(val index: Int) : ConfigUiEvent
    object Save : ConfigUiEvent
}

sealed interface ConfigUiEffect : UiEffect {
    object SaveSuccess : ConfigUiEffect
}