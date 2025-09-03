package cn.changjiahong.banker.tplview.processor

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.model.isColTableType
import cn.changjiahong.banker.model.isTableType
import cn.changjiahong.banker.model.isTextType
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import com.alibaba.excel.EasyExcel
import com.alibaba.excel.enums.WriteDirectionEnum
import com.alibaba.excel.write.metadata.fill.FillConfig
import com.alibaba.excel.write.metadata.fill.FillWrapper
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.lang.Boolean
import java.util.regex.Pattern
import kotlin.Any
import kotlin.Pair
import kotlin.String


actual object ExcelTemplateProcessor : TemplateProcessor {
    actual override fun getFormFields(file: PlatformFile): Flow<List<FormField>> = returnFlow {

        val workbook = XSSFWorkbook(file.file);
        val pattern = Pattern.compile("\\{([^.}]+)(?:\\.[^}]*)?}")

        val fields = mutableSetOf<String>()
        for (sheet in workbook) {
            for (row in sheet) {
                for (cell in row) {
                    val text = cell.toString();
                    val matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        val fieldName = if (matcher.group(1) != null)
                            matcher.group(1) else continue
                        fields.add(fieldName)
                    }
                }
            }
        }

        fields.map { FormField(it, "TEXT") }
    }

    actual override fun fillTemplateForm(
        formData: List<FormFieldValue>,
        templateFile: PlatformFile,
        toTempFile: PlatformFile
    ): Flow<NoData> = okFlow {
        val excelWriter = EasyExcel.write(toTempFile.file).withTemplate(templateFile.file).build()
        val writeSheet = EasyExcel.writerSheet().build()
        val textMap = mutableMapOf<String, String>()
        formData.forEach { f ->
            when {
                f.formFiledType.isTextType() -> {
                    textMap[f.tFieldName] = f.fieldValue
                }

                f.formFiledType.isTableType() -> {
                    val table =
                        Json.decodeFromString<List<Map<String, String>>>(f.fieldValue)
                    val fillConfigBuilder = FillConfig.builder()
                        .forceNewRow(Boolean.TRUE)
                    if (f.formFiledType.isColTableType()) {
                        fillConfigBuilder.direction(WriteDirectionEnum.HORIZONTAL)
                    }
                    val fillConfig = fillConfigBuilder.build()
                    excelWriter.fill(FillWrapper(f.tFieldName, table), fillConfig, writeSheet)
                }

            }
        }
        excelWriter.fill(textMap, writeSheet).finish()
    }

}