package cn.changjiahong.banker.app.template

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import banker.composeapp.generated.resources.Res
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.pdfutils.PdfTemplateProcessor

@Composable
fun DOCTemplateView(templateFillerData: List<TemplateFillerItem>) {

}

@Composable
fun PDFTemplateView(templateFillerData: List<TemplateFillerItem>) {
    var tempPath by remember { mutableStateOf("") }


    LaunchedEffect(Unit){
        PdfTemplateProcessor.fillTemplate(emptyMap()).collect {
            tempPath = it
        }
        val tp =  Res.getUri("files/《个人征信业务授权书》_模版.pdf")
        println(tp)
        tempPath=tp

    }
//
    Text(tempPath)

//        if (tempPath.isNotEmpty()) {
//            PDFViewer(
//                tempPath,
//                modifier = Modifier.width(300.dp)
//            )
//        }
}