package cn.changjiahong.banker

import org.koin.core.context.startKoin

/**
 *
 * @author ChangJiahong
 * @date 2025/2/9
 */
// Helper.kt

fun initKoin(){
    startKoin {
        modules(appModules)
    }
}