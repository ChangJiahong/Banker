package cn.changjiahong.banker

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.composable.TipDialog
import cn.changjiahong.banker.composable.rememberTipState
import cn.changjiahong.banker.uieffect.NavigatorEffectRegister
import cn.changjiahong.banker.uieffect.TipDialogRegister
import cn.changjiahong.banker.uieffect.ToastRegister
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.ksp.generated.module

val GlobalNavigator: ProvidableCompositionLocal<Navigator> =
    staticCompositionLocalOf { error("GlobalNavigator not initialized") }

val appModules: List<Module>
    get() = listOf(
        AppKoin.module
    )

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    Navigator(RR.LOGIN) { globalNavigator ->
        CompositionLocalProvider(GlobalNavigator providesDefault globalNavigator) {

            NavigatorEffectRegister(globalNavigator)
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            ToastRegister(snackbarHostState, scope)
            val tipDialogState = rememberTipState()
            TipDialogRegister(tipDialogState)

            MaterialTheme {
                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) {
                    CurrentScreen()
                }
                TipDialog(tipDialogState)
            }

        }
    }
}
