package cn.changjiahong.banker.template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.changjiahong.banker.app.template.DOCTemplateView
import cn.changjiahong.banker.pdfutils.PDFViewer
import cn.changjiahong.banker.platform.SystemOpen
import cn.changjiahong.banker.storage.FileType
import cn.changjiahong.banker.storage.FileType.DOC
import cn.changjiahong.banker.storage.FileType.DOCX
import cn.changjiahong.banker.storage.FileType.PDF
import cn.changjiahong.banker.storage.FileType.XLS
import cn.changjiahong.banker.storage.FileType.XLSX
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.extension

@Composable
fun FilePreView(file: PlatformFile) {
    if (!file.exists()) {
        return
    }
    when (FileType.getFileType(file.extension)) {
        PDF -> {
            PDFViewer(
                file,
                modifier = Modifier.width(300.dp)
            )
        }

        DOC, DOCX, XLS, XLSX -> SystemOpen(file)
        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("未知的文件类型，尚不受支持。请联系管理员！！！")
        }
    }
}
