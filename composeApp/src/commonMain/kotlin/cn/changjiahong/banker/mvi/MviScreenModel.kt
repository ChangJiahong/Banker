package cn.changjiahong.banker.mvi

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.uieffect.DIREffect
import cn.changjiahong.banker.uieffect.GoDIREffect
import cn.changjiahong.banker.uieffect.GoDIREvent
import cn.changjiahong.banker.uieffect.UiEffectDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

abstract class MviScreenModel : ScreenModel, KoinComponent {
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    private val uiEvent = _uiEvent.asSharedFlow()

    private val _uiEffect = MutableSharedFlow<UiEffect>()
    private val uiEffect = _uiEffect.asSharedFlow()

    private var _handleEffect: (effect: UiEffect) -> Unit =
        { UiEffectDispatcher.dispatchEffect(it) }


    init {
        screenModelScope.launch(Dispatchers.Main) {
            uiEvent.collect { value ->
                when (value) {
                    is GoDIREvent -> {
                        GoDIREffect(value.screen, value.isReplace).trigger()
                    }
                    else ->
                        handleEvent(value)
                }
            }
        }
        screenModelScope.launch(Dispatchers.Main) {
            uiEffect.collect { value ->
                _handleEffect(value)
            }
        }
    }

    fun sendEvent(intent: UiEvent) {
        screenModelScope.launch {
            _uiEvent.emit(intent)
        }
    }

    fun sendEffect(intent: UiEffect) {
        screenModelScope.launch {
            _uiEffect.emit(intent)
        }
    }

    fun UiEffect.trigger() {
        sendEffect(this)
    }


    protected abstract fun handleEvent(event: UiEvent)

    open fun handleEffect(handle: UiEffectHandler) {
        val oldHandler = _handleEffect
        _handleEffect = { if (!handle(it)) oldHandler(it) }
    }
}