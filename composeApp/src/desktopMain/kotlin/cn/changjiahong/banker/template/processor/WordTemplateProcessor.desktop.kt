package cn.changjiahong.banker.template.processor

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.storage.Storage
import cn.changjiahong.banker.template.TemplateProcessor
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.template.run.RunTemplate
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.getScopeName
import java.io.FileOutputStream


actual object WordTemplateProcessor : TemplateProcessor {
    actual override fun getFormFields(file: PlatformFile): Flow<List<FormField>> = returnFlow {
        val template = XWPFTemplate.compile(file.file)
        val elements = template.elementTemplates
        elements.map {
            when {
                it is RunTemplate -> FormField(it.tagName, "TEXT")
                else -> FormField(it.variable(), "")
            }
        }
    }

    actual override fun fillTemplateForm(
        formData: List<FormFieldValue>,
        templateFile: PlatformFile,
        toTempFile: PlatformFile
    ): Flow<NoData> = okFlow {
        val template = XWPFTemplate.compile(templateFile.file).render(
            formData.associate { Pair(it.tFieldName, it.fieldValue) }
        )
        template.writeAndClose(toTempFile.file.outputStream())
    }
}