package cn.changjiahong.banker.uieffect

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Scope

/**
 *
 * @author ChangJiahong
 * @date 2025/2/8
 */


sealed interface DefaultEffect : UiEffect

class GoEffect(val screen: Screen, val isReplace: Boolean = false) : DefaultEffect


sealed interface DefaultEvent : UiEvent

class GoEvent(val screen: Screen, val isReplace: Boolean = false) : DefaultEvent

class ShowSnackbar(val text: String) : DefaultEffect

class ShowSnack(val text: String) : DefaultEvent


@Composable
fun NavigatorEffectRegister(globalNavigator: Navigator) {
    UiEffectDispatcher.register { effect ->
        if (effect is GoEffect) {
            if (effect.isReplace) {
                globalNavigator.replace(effect.screen)
            } else {
                globalNavigator += effect.screen
            }
            true
        }
        false
    }
}

@Composable
fun ShowSnackbarRegister(snackbarHostState: SnackbarHostState,scope: CoroutineScope) {
    UiEffectDispatcher.register { effect ->
        if (effect is ShowSnackbar) {
            scope.launch {
                snackbarHostState.showSnackbar(effect.text)
            }
            true
        }
        false
    }
}