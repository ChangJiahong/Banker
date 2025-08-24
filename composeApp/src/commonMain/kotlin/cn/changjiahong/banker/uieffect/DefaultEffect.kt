package cn.changjiahong.banker.uieffect

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.composable.TipState
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *
 * @author ChangJiahong
 * @date 2025/2/8
 */


sealed interface DefaultEffect : UiEffect
sealed interface DefaultEvent : UiEvent

class GoEffect(val screen: Screen, val isReplace: Boolean = false) : DefaultEffect

class GoEvent(val screen: Screen, val isReplace: Boolean = false) : DefaultEvent

class ShowToast(val text: String) : DefaultEffect
class ShowTip(val tip: String) : DefaultEffect

class Toast(val text: String) : DefaultEvent
class Tip(val tip: String) : DefaultEvent


@Composable
fun NavigatorEffectRegister(globalNavigator: Navigator) {
    UiEffectDispatcher.register { effect ->
        return@register if (effect is GoEffect) {
            if (effect.isReplace) {
                globalNavigator.replace(effect.screen)
            } else {
                globalNavigator += effect.screen
            }
            true
        } else false
    }
}

@Composable
fun ToastRegister(snackbarHostState: SnackbarHostState, scope: CoroutineScope) {
    UiEffectDispatcher.register { effect ->
        return@register if (effect is ShowToast) {
            scope.launch {
                snackbarHostState.showSnackbar(effect.text)
            }
            true
        } else false
    }
}

@Composable
fun TipDialogRegister(tipDialogState: TipState) {
    UiEffectDispatcher.register { effect ->
        return@register if (effect is ShowTip) {
            tipDialogState.show(effect.tip)
            true
        } else false
    }
}