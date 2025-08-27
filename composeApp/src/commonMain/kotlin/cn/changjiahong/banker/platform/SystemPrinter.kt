package cn.changjiahong.banker.platform

import cn.changjiahong.banker.model.NoData
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

expect object SystemPrinter {
    fun print(file: PlatformFile): Flow<NoData>
}

