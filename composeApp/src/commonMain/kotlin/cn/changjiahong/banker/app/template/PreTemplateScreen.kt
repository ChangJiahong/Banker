package cn.changjiahong.banker.app.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.arrow_back
import cafe.adriel.voyager.core.screen.Screen
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.GlobalNavigator
import cn.changjiahong.banker.storage.platformFile
import cn.changjiahong.banker.template.FilePreView
import org.jetbrains.compose.resources.painterResource

class PreTemplateScreen(val template: Template): Screen {
    private val PDF = "PDF"
    private val DOC = "DOC"
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val globalNavigator = GlobalNavigator.current
        Scaffold(
            topBar = {
                TopAppBar({
                    Text("模版文件维护")
                }, navigationIcon = {
                    IconButton(onClick = {
                        globalNavigator.pop()
                    }, modifier = Modifier) {
                        Icon(
                            painter = painterResource(Res.drawable.arrow_back),
                            contentDescription = "arrow back"
                        )
                    }
                })
            }
        ) { paddingValues ->

            Box(Modifier.padding(paddingValues)) {
                FilePreView(template.filePath.platformFile)
//                when (template.fileType) {
//                    PDF -> PDFTemplateView(template)
//                    DOC -> DOCTemplateView(template)
//                    else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        Text("未知的文件类型，尚不受支持。请联系管理员！！！")
//                    }
//                }
            }
        }
    }
}