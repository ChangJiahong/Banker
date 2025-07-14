package cn.changjiahong.banker.pdfutils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter

@Composable
fun PDFViewer(
    pdfFilePath: String, modifier: Modifier = Modifier,
) {
    val pageStates = remember { mutableStateListOf<PdfRenderEvent.Page?>() }
    var totalPages by remember { mutableIntStateOf(0) }

    LaunchedEffect(pdfFilePath) {
        PDFRenderer.renderFlow(pdfFilePath).collect { event ->
            when (event) {
                is PdfRenderEvent.Meta -> {
                    totalPages = event.pageCount
                    pageStates.clear()
                    repeat(totalPages) { pageStates.add(null) }
                }

                is PdfRenderEvent.Page -> {
                    pageStates[event.index] = event
                }

                is PdfRenderEvent.Error -> {
                    // 可以打印或显示 error 信息
                }
            }
        }
    }

    PapersViewer(papers = pageStates.map {
        Paper(if (it != null) BitmapPainter(it.image) else null)
    })

}
