package cn.changjiahong.banker.platform

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun HorizontalScrollbar(
    scrollState: ScrollState,
    modifier: Modifier,
    reverseLayout: Boolean
) {
    androidx.compose.foundation.HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(scrollState),
        modifier = modifier
        )
}