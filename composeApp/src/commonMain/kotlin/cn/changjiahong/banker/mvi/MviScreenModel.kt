package cn.changjiahong.banker.mvi

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.uieffect.DIREffect
import cn.changjiahong.banker.uieffect.GoDIREffect
import cn.changjiahong.banker.uieffect.GoDIREvent
import cn.changjiahong.banker.uieffect.GoEffect
import cn.changjiahong.banker.uieffect.GoEvent
import cn.changjiahong.banker.uieffect.ShowSnack
import cn.changjiahong.banker.uieffect.ShowSnackbar
import cn.changjiahong.banker.uieffect.UiEffectDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
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

                    is GoEvent -> {
                        GoEffect(value.screen, value.isReplace).trigger()
                    }

                    is ShowSnack -> {
                        snack(value.text)
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

    suspend fun <T> Flow<T>.catchAndCollect(collector: FlowCollector<T>) {
        catch {
            snack(it.message ?: "")
        }.collect(collector)
    }

    fun snack(text: String) {
        ShowSnackbar(text).trigger()
    }
}

inline fun <T> MutableStateFlow<List<T>>.replace(
    index: Int,
    transform: (T) -> T
) {
    this.update { list ->
        list.toMutableList().apply {
            this[index] = transform(this[index])
        }
    }
}