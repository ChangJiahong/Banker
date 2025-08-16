package cn.changjiahong.banker.app.about.settings

import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent

sealed interface ConfigUiEvent : UiEvent {
    object Add : ConfigUiEvent
    object Delete : ConfigUiEvent
    class Update<T>(val index: Int, val item: T) : ConfigUiEvent
}

sealed interface ConfigUiEffect : UiEffect {
    object SaveSuccess : ConfigUiEffect
}