package cn.changjiahong.banker.pdfutils

import androidx.compose.ui.text.platform.Font
import cn.changjiahong.banker.storage.Storage
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.forms.fields.properties.CheckBoxType
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
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


}