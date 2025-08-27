package cn.changjiahong.banker.tplview.processor

import cn.changjiahong.banker.model.FormField
import cn.changjiahong.banker.model.FormFieldValue
import cn.changjiahong.banker.model.NoData
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import com.alibaba.excel.EasyExcel
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.flow.Flow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.regex.Pattern

actual object ExcelTemplateProcessor : TemplateProcessor {
    actual override fun getFormFields(file: PlatformFile): Flow<List<FormField>> = returnFlow {

        val workbook = XSSFWorkbook(file.file);
        val pattern = Pattern.compile("\\{([^}]+)\\}")
        val fields = mutableListOf<FormField>()
        for (sheet in workbook) {
            for (row in sheet) {
                for (cell in row) {
                    val text = cell.toString();
                    val matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        val fieldName = if (matcher.group(1) != null)
                            matcher.group(1) else matcher.group(2)
                        fields.add(FormField(fieldName, "TEXT"))
                    }
                }
            }
        }

        fields
    }

    actual override fun fillTemplateForm(
        formData: List<FormFieldValue>,
        templateFile: PlatformFile,
        toTempFile: PlatformFile
    ): Flow<NoData> = okFlow {
        EasyExcel.write(toTempFile.file)
            .withTemplate(templateFile.file)
            .sheet().doFill(formData.associate { Pair(it.tFieldName,it.fieldValue) });
    }
}