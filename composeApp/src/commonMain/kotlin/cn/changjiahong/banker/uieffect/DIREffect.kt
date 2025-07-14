package cn.changjiahong.banker.uieffect

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cn.changjiahong.banker.mvi.UiEvent

sealed interface DIREffect : DefaultEffect

class GoDIREffect(val screen: Screen, val isReplace: Boolean = false): DIREffect


@Composable
fun DIREffectRegister(globalNavigator: Navigator) {
    UiEffectDispatcher.register { effect ->
        if (effect is GoDIREffect) {
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

sealed interface DIRUiEvent : UiEvent {
}

class GoDIREvent(val screen: Screen, val isReplace: Boolean = false) : DIRUiEvent