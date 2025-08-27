package cn.changjiahong.banker.tplview

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.storage.FileType
import cn.changjiahong.banker.tplview.processor.ExcelTemplateProcessor
import cn.changjiahong.banker.tplview.processor.PDFTempProcessor
import cn.changjiahong.banker.tplview.processor.TemplateProcessor
import cn.changjiahong.banker.tplview.processor.WordTemplateProcessor
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import kotlinx.coroutines.flow.Flow


object TemplateKit : TemplateProcessor {

    override fun getFormFields(file: PlatformFile): Flow<List<FormField>> {
        val processor = file.getTempProcessor()
        return processor.getFormFields(file)
    }

    override fun fillTemplateForm(
        formData: List<FormFieldValue>,
        templateFile: PlatformFile,
        toTempFile: PlatformFile
    ): Flow<NoData> {
        val processor = templateFile.getTempProcessor()
        return processor.fillTemplateForm(formData, templateFile, toTempFile)
    }

    private fun PlatformFile.getTempProcessor(): TemplateProcessor {
        return when (FileType.getFileType(extension)) {
            FileType.PDF -> PDFTempProcessor
            FileType.DOC, FileType.DOCX -> WordTemplateProcessor
            FileType.XLS, FileType.XLSX -> ExcelTemplateProcessor
            else -> throw Exception("不受支持的文件类型")
        }
    }
}

