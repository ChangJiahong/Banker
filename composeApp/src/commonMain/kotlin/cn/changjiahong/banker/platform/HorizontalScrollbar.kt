package cn.changjiahong.banker.platform

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun HorizontalScrollbar(scrollState: ScrollState,
                               modifier: Modifier = Modifier,
                               reverseLayout: Boolean = false)