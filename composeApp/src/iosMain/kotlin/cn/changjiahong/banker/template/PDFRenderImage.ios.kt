package cn.changjiahong.banker.pdfutils

import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

actual object PDFRenderer {
    actual fun renderFlow(pdfFile: PlatformFile): Flow<PdfRenderEvent> {
        TODO("Not yet implemented")
    }
}