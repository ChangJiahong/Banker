package cn.changjiahong.banker.app.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.model.Field
import cn.changjiahong.banker.template.FilePreView
import org.koin.core.parameter.parametersOf

class TemplateScreen(
    val userId: Long,
    val businessId: Long,
    val template: Template,
    val fields: List<Field>
) :
    DirScreen {
    override val dirName: String
        get() = template.templateName

    private val PDF = "PDF"
    private val DOC = "DOC"

    @Composable
    override fun Content() {

        val templateScreenModel = koinScreenModel<TemplateScreenModel>(parameters = {
            parametersOf(
                userId,
                businessId,
                template,
            )
        })

       val cacheFile by templateScreenModel.cacheFile.collectAsState()

        if (cacheFile!=null){
            FilePreView(cacheFile!!)
        }

    }


}