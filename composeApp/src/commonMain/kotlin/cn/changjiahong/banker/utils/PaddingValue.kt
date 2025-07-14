package cn.changjiahong.banker.utils

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class PaddingValue(
    var start: Dp = 0.dp,
    var top: Dp = 0.dp,
    var end: Dp = 0.dp,
    var bottom: Dp = 0.dp
) {
    fun paddingStart(start: Dp) {
        this.start = start
    }

    fun paddingTop(top: Dp) {
        this.top = top
    }

    fun paddingEnd(end: Dp) {
        this.end = end
    }

    fun paddingBottom(bottom: Dp) {
        this.bottom = bottom
    }

    fun paddingHorizontal(horizontal: Dp) {
        this.start = horizontal
        this.end = horizontal
    }

    fun paddingVertical(vertical: Dp) {
        this.top = vertical
        this.bottom = vertical
    }

    fun toPadding(): PaddingValues {
        return PaddingValues(start, top, end, bottom)
    }
}

@Stable
fun Modifier.padding(block: PaddingValue.() -> Unit):Modifier {
    val paddingValue = PaddingValue()
    block(paddingValue)
    return padding(paddingValue.toPadding())
}
