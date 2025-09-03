package cn.changjiahong.banker.tplview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.changjiahong.banker.storage.FileType
import cn.changjiahong.banker.storage.FileType.PDF
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
        else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("未知的文件类型，尚不受支持。请联系管理员！！！")
        }
    }
}
