package cn.changjiahong.banker.pdfutils

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow

expect object PDFRenderer {
    fun renderFlow(pdfPath: String): Flow<PdfRenderEvent>
}

sealed interface PdfRenderEvent {
    data class Meta(val pageCount: Int) : PdfRenderEvent
    data class Page(val index: Int, val image: ImageBitmap) : PdfRenderEvent
    data class Error(val index: Int?, val error: Throwable) : PdfRenderEvent
}
