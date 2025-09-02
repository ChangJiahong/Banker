package cn.changjiahong.banker.tplview.processor

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.storage.platformFile
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.config.Configure
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy
import com.deepoove.poi.template.run.RunTemplate
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow


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

        val table: MutableList<Map<String, String>> = mutableListOf()
        table.add(mapOf(
            Pair("name","这是"),
            Pair("age","12"),
            Pair("clazz","八十")
        ))
        table.add(mapOf(
            Pair("name","阿萨"),
            Pair("age","22"),
            Pair("clazz","爱上")
        ))
        table.add(mapOf(
            Pair("name","但是"),
            Pair("age","22"),
            Pair("clazz","爱上")
        ))
        table.add(mapOf(
            Pair("name","皮带"),
            Pair("age","22"),
            Pair("clazz","爱上")
        ))




        // 填充数据
        val data: MutableMap<String, Any> = HashMap()
        data.put("user", table)
        val policy = LoopRowTableRenderPolicy()

        val config = Configure.builder()
            .bind("user", policy).build()

        val template = XWPFTemplate.compile("D:\\ChangJiahongs\\Documents\\模板.docx".platformFile.file,config).render(
            data
        )
        template.writeAndClose(toTempFile.file.outputStream())
    }
}