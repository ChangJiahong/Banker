package cn.changjiahong.banker.uieffect

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cn.changjiahong.banker.mvi.UiEffect

/**
 *
 * @author ChangJiahong
 * @date 2025/2/8
 */


sealed interface DefaultEffect : UiEffect

class GoEffect(val screen: Screen, val isReplace: Boolean = false) : DefaultEffect


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