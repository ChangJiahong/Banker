package cn.changjiahong.banker.template

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.storage.Storage
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.net.URI

actual object PDFTempProcessor : TemplateProcessor {
    actual override fun getFormFields(file: PlatformFile): Flow<List<FormField>> = flow {
        val pdfReader = PdfReader(file.file)
        val pdfDoc = PdfDocument(pdfReader)
        val form = PdfAcroForm.getAcroForm(pdfDoc, false)

        val formFields = mutableListOf<FormField>()
        form.allFormFields.forEach { name, form ->
            val name = form.fieldName.value
            val type = when (form.formType.value) {
                "Tx" -> "TEXT"
                "Btn" -> "CHECK"
                else -> ""
            }
            formFields += FormField(name, type)
        }
        emit(formFields)
    }
}