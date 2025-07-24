package cn.changjiahong.banker.app.template

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.pdfutils.PDFViewer
import cn.changjiahong.banker.pdfutils.PdfTemplateProcessor

@Composable
fun DOCTemplateView(
    template: DocTemplate,
    templateFillerData: List<TemplateFillerItem> = emptyList()
) {

    LaunchedEffect(Unit){
        PdfTemplateProcessor.fillWordTemplate(mapOf(),"filePath").collect{
//            tempPath = it
            println("word")
        }
    }
}

@Composable
fun PDFTemplateView(
    template: DocTemplate,
    templateFillerData: List<TemplateFillerItem> = emptyList()
) {
    var tempPath by remember { mutableStateOf("") }

    val fieldMap = mutableMapOf<String, String>()

    templateFillerData.forEach {
        fieldMap.put(it.fieldName, it.fieldValue)
    }

    LaunchedEffect(Unit) {

        val filePath = if (template.filePath.startsWith("res:/")) {
            Res.getUri(template.filePath.substringAfter("res:/"))
        } else {
            "file:${template.filePath}"
        }


        if (fieldMap.isEmpty()){
            tempPath = filePath.substringAfter("file:")
        }else {
            PdfTemplateProcessor.fillTemplate(fieldMap, filePath).collect {
                tempPath = it
            }
        }
//        val tp =  Res.getUri("files/001.pdf")
//        println(tp)
//        tempPath=tp

    }
//
//    Text(tempPath)

    if (tempPath.isNotEmpty()) {
        PDFViewer(
            tempPath,
            modifier = Modifier.width(300.dp)
        )
    }
}