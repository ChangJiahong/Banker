package cn.changjiahong.banker.mvi

interface UiEvent {
    fun sendTo(viewModel: MviScreenModel) {
        viewModel.sendEvent(this)
    }
}

interface UiState
interface UiEffect

public typealias UiEffectHandler = (effect: UiEffect) -> Boolean

