package cn.changjiahong.banker.pdfutils

import kotlinx.coroutines.flow.Flow

actual object PDFRenderer {
    actual fun renderFlow(pdfPath: String): Flow<PdfRenderEvent> {
        TODO("Not yet implemented")
    }
}