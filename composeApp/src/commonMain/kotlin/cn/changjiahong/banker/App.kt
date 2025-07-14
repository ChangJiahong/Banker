package cn.changjiahong.banker

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.app.main.MainScreen
import cn.changjiahong.banker.uieffect.NavigatorEffectRegister
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.ksp.generated.module

val GlobalNavigator: ProvidableCompositionLocal<Navigator> =
    staticCompositionLocalOf { error("GlobalNavigator not initialized") }

val appModules: List<Module>
    get() = listOf(AppKoin.module)

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    Navigator(RR.MAIN) { globalNavigator ->
        CompositionLocalProvider(GlobalNavigator providesDefault globalNavigator) {

            NavigatorEffectRegister(globalNavigator)

            MaterialTheme {
                CurrentScreen()
            }

        }
    }
}
