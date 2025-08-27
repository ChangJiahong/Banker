package cn.changjiahong.banker.tplview

import androidx.compose.ui.graphics.ImageBitmap
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

expect object PDFRenderer {
    fun renderFlow(pdfFile: PlatformFile): Flow<PdfRenderEvent>
}

sealed interface PdfRenderEvent {
    data class Meta(val pageCount: Int) : PdfRenderEvent
    data class Page(val index: Int, val image: ImageBitmap) : PdfRenderEvent
    data class Error(val index: Int?, val error: Throwable) : PdfRenderEvent
}
