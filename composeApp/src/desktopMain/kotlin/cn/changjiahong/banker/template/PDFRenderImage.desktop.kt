package cn.changjiahong.banker.pdfutils

import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield
import org.apache.pdfbox.Loader
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import java.io.File

actual object PDFRenderer {

    actual fun renderFlow(pdfFile: PlatformFile): Flow<PdfRenderEvent> = flow {
        val doc = Loader.loadPDF(pdfFile.file)
        val renderer = PDFRenderer(doc)
        val total = doc.numberOfPages

        emit(PdfRenderEvent.Meta(total))

        for (i in 0 until total) {
            try {
                val img = renderer.renderImageWithDPI(i, 300f, ImageType.RGB)
                emit(PdfRenderEvent.Page(i, img.toComposeImageBitmap()))
            } catch (e: Exception) {
                emit(PdfRenderEvent.Error(i, e))
            }
            yield() // 释放给 UI 线程调度
        }

        doc.close()
    }
}