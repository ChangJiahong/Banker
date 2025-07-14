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

actual object PdfTemplateProcessor {
//    override val type = TemplateType.PDF

    actual suspend fun fillTemplate(fieldMap: Map<String, String>): Flow<String> = flow {
        val pdfReader =
            PdfReader(File("/Volumes/Ti600/Users/changjiahong/Documents/《个人征信业务授权书》_模版.pdf"))
        val writer = PdfWriter(File("/Volumes/Ti600/Users/changjiahong/Documents/111.pdf"))
        val pdfDoc = PdfDocument(pdfReader, writer)
        val form = PdfAcroForm.getAcroForm(pdfDoc, false)
        val result = mutableListOf<String>()


        form.getField("checkbox1").setValue("yes")
        form.getField("id_type").setValue("身份证")
        form.getField("id_num").setValue("341126199711066356")

        form.flattenFields()
        pdfDoc.close()
        emit("/Volumes/Ti600/Users/changjiahong/Documents/111.pdf")
    }


}