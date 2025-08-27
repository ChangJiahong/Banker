package cn.changjiahong.banker.tplview.processor

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.utils.okFlow
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    actual override fun fillTemplateForm(
        formData: List<FormFieldValue>,
        templateFile: PlatformFile,
        toTempFile: PlatformFile
    ): Flow<NoData> = okFlow {
        val pdfReader =
            PdfReader(templateFile.file)
        val writer = PdfWriter(toTempFile.file)
        val pdfDoc = PdfDocument(pdfReader, writer)
        val form = PdfAcroForm.getAcroForm(pdfDoc, false)

        formData.forEach {
            form.getField(it.tFieldName)?.setValue(it.fieldValue)
        }

        form.flattenFields()
        pdfDoc.close()

    }
}