package cn.changjiahong.banker.pdfutils

import androidx.compose.ui.text.platform.Font
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
        val writer = PdfWriter(File("/Volumes/Ti600/Users/changjiahong/Documents/111.pdf"))
        val pdfDoc = PdfDocument(pdfReader, writer)
        val form = PdfAcroForm.getAcroForm(pdfDoc, false)
        val result = mutableListOf<String>()

        fieldMap.forEach {
            form.getField(it.key).setValue(it.value)
        }

        form.getField("checkbox7").setValue("yes")

        form.flattenFields()
        pdfDoc.close()
        emit("/Volumes/Ti600/Users/changjiahong/Documents/111.pdf")
    }


}