package cn.changjiahong.banker.template

import cn.changjiahong.banker.storage.Storage
import com.deepoove.poi.XWPFTemplate
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.net.URI


actual object PdfTemplateProcessor {
//    override val type = TemplateType.PDF

    actual suspend fun fillTemplate(fieldMap: Map<String, String>,fileUri: String): Flow<String> = flow {
        val pdfReader =
            PdfReader(File(URI(fileUri)))
        val tempFilePath = Storage.getTempFilePath("11.pdf")
        val writer = PdfWriter(File(tempFilePath))
        val pdfDoc = PdfDocument(pdfReader, writer)
        val form = PdfAcroForm.getAcroForm(pdfDoc, false)

        fieldMap.forEach {
            form.getField(it.key)?.setValue(it.value)
        }

        form.flattenFields()
        pdfDoc.close()
        emit(tempFilePath)
    }

    actual suspend fun fillWordTemplate(fieldMap: Map<String, String>,fileUri: String): Flow<String> =
        flow {

            val wt = Storage.appDir + "/特约商户资料变更登记表.docx"

            val template = XWPFTemplate.compile(wt).render(
                mapOf(
                    Pair("businessName", "商户_李杨"),
                    Pair("name", "李杨")
                )
            )
            template.writeAndClose(FileOutputStream(Storage.tempDir + "/output.docx"))

            if (Desktop.isDesktopSupported()) {
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(File(Storage.tempDir + "/output.docx"))
                } else {
                    println("当前系统不支持打开文件操作")
                }
            } else {
                println("Desktop 不支持")
            }
            emit("")
        }
            .flowOn(Dispatchers.IO)


}