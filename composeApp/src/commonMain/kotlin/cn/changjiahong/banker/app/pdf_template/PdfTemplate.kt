package cn.changjiahong.banker.app.pdf_template

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
import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.pdfutils.PDFViewer
import cn.changjiahong.banker.pdfutils.PdfTemplateProcessor
import cn.changjiahong.banker.storage.Storage
import cn.changjiahong.banker.storage.StorageProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object PdfTemplateScreen : DirScreen {
    override val dirName: String
        get() = "个人征信"

    @OptIn(DelicateCoroutinesApi::class)
    @Composable
    override fun Content() {

        var tempPath by remember { mutableStateOf("") }


        LaunchedEffect(Unit){
            PdfTemplateProcessor.fillTemplate(emptyMap()).collect {
                tempPath = it
            }
           val tp =  Res.getUri("files/《个人征信业务授权书》_模版.pdf")
            println(tp)
            tempPath=tp

        }

        Text(tempPath)

//        if (tempPath.isNotEmpty()) {
//            PDFViewer(
//                tempPath,
//                modifier = Modifier.width(300.dp)
//            )
//        }
    }
}