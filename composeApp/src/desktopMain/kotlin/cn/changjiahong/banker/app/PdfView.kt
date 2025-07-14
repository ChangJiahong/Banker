package cn.changjiahong.banker.app

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.apache.pdfbox.Loader
import java.io.File

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import javax.imageio.ImageIO


@Composable
fun PdfView(){
//    val pdfReader = PdfReader

    Button({

        val pdfFile = File("/Volumes/Ti600/Users/changjiahong/Documents/李二庄/3《个人征信业务授权书》.pdf")
        renderPdfToImage("/Volumes/Ti600/Users/changjiahong/Documents/李二庄/3《个人征信业务授权书》.pdf",
            "//Volumes/Ti600/Users/changjiahong/Documents/李二庄/1.png", 330f);

        println("pdfRender has completed!")
    }){
        Text("PDF")
    }

}


fun renderPdfToImage(inputPath: String, outputPath: String, dpi: Float = 144f) {
    val document = Loader.loadPDF(File(inputPath))
    val renderer = PDFRenderer(document)
    val bim = renderer.renderImageWithDPI(0, 300f, ImageType.RGB) // 渲染第 0 页

    ImageIO.write(bim, "PNG", File(outputPath))
    document.close()
}
