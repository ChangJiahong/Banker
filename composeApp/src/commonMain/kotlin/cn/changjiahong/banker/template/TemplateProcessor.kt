package cn.changjiahong.banker.template

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.storage.FileType
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import kotlinx.coroutines.flow.Flow

interface TemplateProcessor {
    fun getFormFields(file: PlatformFile): Flow<List<FormField>>
}

expect object PDFTempProcessor : TemplateProcessor {
    override fun getFormFields(file: PlatformFile): Flow<List<FormField>>

}


object TemplateKit: TemplateProcessor {

    override fun getFormFields(file: PlatformFile): Flow<List<FormField>> {
        val processor = file.getTempProcessor()
        return processor.getFormFields(file)
    }


    private fun PlatformFile.getTempProcessor(): TemplateProcessor {
        return when (FileType.getFileType(extension)) {
            FileType.PDF -> PDFTempProcessor
            else -> throw Exception("不受支持的文件类型")
        }
    }
}

