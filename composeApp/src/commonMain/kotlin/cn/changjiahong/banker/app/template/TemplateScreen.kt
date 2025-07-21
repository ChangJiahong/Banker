package cn.changjiahong.banker.app.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.BusinessField
import cn.changjiahong.banker.BusinessFieldValue
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.TemplateField
import cn.changjiahong.banker.UserExtendField
import cn.changjiahong.banker.UserExtendFieldValue
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.model.TemplateFillerItem
import org.koin.core.parameter.parametersOf

class TemplateScreen(val businessId: Long, val template: DocTemplate, val userId: Long) :
    DirScreen {
    override val dirName: String
        get() = template.templateName

    private val PDF = "PDF"
    private val DOC = "DOC"

    @Composable
    override fun Content() {

        val templateScreenModel = koinScreenModel<TemplateScreenModel>(parameters = { parametersOf(businessId,template,userId) })

        val fieldsMap = emptyMap<String, String>()

        val businessFieldsMap = emptyMap<BusinessField, BusinessFieldValue>()
        val userFieldsMap = emptyMap<UserExtendField, UserExtendFieldValue>()

        val templateFields = emptyList<TemplateField>()


        val templateFillerData: List<TemplateFillerItem> = listOf()




        when (template.fileType) {
            PDF -> PDFTemplateView(templateFillerData)
            DOC -> DOCTemplateView(templateFillerData)
            else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("未知的文件类型，尚不受支持。请联系管理员！！！")
            }
        }
    }


}