package cn.changjiahong.banker.platform

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.PlatformFile

@Composable
expect fun SystemOpen(file: PlatformFile)