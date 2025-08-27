package cn.changjiahong.banker.app.template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.DirScreen
import cn.changjiahong.banker.model.Field
import cn.changjiahong.banker.tplview.FilePreView
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.nameWithoutExtension
import org.koin.core.parameter.parametersOf

class TemplateScreen(
    val file: PlatformFile
) :
    DirScreen {
    override val dirName: String
        get() = file.nameWithoutExtension

    private val PDF = "PDF"
    private val DOC = "DOC"

    @Composable
    override fun Content() {

        FilePreView(file)

    }


}