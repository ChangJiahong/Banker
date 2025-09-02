package cn.changjiahong.banker.tplview.processor

import cn.changjiahong.banker.model.COL_TABLE
import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.ROW_TABLE
import cn.changjiahong.banker.model.isRowTableType
import cn.changjiahong.banker.model.isTableType
import cn.changjiahong.banker.model.isTextType
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.config.Configure
import com.deepoove.poi.data.Tables
import com.deepoove.poi.data.style.BorderStyle
import com.deepoove.poi.plugin.table.LoopColumnTableRenderPolicy
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy
import com.deepoove.poi.template.run.RunTemplate
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json


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


        // 填充数据
        val data: MutableMap<String, Any> = HashMap()
        val config = Configure.builder()


        formData.forEach { f ->
            when {
                f.formFiledType.isTextType() -> {
                    data.put(f.tFieldName, f.fieldValue)
                }

                f.formFiledType.isTableType() -> {
                    val table =
                        Json.decodeFromString<List<Map<String, String>>>(f.fieldValue)

                    when (f.formFiledType) {
                        ROW_TABLE, COL_TABLE -> {
                            config.bind(
                                f.tFieldName,
                                if (f.formFiledType.isRowTableType()) LoopRowTableRenderPolicy() else LoopColumnTableRenderPolicy()
                            )
                            data.put(f.tFieldName, table)
                        }

                        else -> {
                            val tableArray = table.map { it.values.toTypedArray() }.toTypedArray()
                            data.put(
                                f.tFieldName,
                                Tables.of(tableArray).border(BorderStyle.DEFAULT).create()
                            )
                        }
                    }

                }
            }
        }


        val template = XWPFTemplate.compile(
            templateFile.file,
            config.build()
        ).render(data)

        template.writeAndClose(toTempFile.file.outputStream())
    }
}