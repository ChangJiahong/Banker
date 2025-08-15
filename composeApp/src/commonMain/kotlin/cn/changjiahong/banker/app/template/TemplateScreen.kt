package cn.changjiahong.banker.app.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.DirScreen
import org.koin.core.parameter.parametersOf

class TemplateScreen(val businessId: Long, val template: Template, val userId: Long) :
    DirScreen {
    override val dirName: String
        get() = template.templateName

    private val PDF = "PDF"
    private val DOC = "DOC"

    @Composable
    override fun Content() {

        val templateScreenModel = koinScreenModel<TemplateScreenModel>(parameters = { parametersOf(businessId,template,userId) })


        val templateFillerData by templateScreenModel.templateFillerData.collectAsState()


        when (template.fileType) {
//            PDF -> PDFTemplateView(template,templateFillerData)
            PDF -> DOCTemplateView(template,templateFillerData)
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("未知的文件类型，尚不受支持。请联系管理员！！！")
            }
        }
    }


}