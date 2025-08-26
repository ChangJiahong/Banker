package cn.changjiahong.banker.template.processor

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.template.TemplateProcessor
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow

expect object WordTemplateProcessor: TemplateProcessor {

    override fun getFormFields(file: PlatformFile): Flow<List<FormField>>

    override fun fillTemplateForm(
        formData: List<FormFieldValue>,
        templateFile: PlatformFile,
        toTempFile: PlatformFile
    ): Flow<NoData>
}