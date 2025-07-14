package cn.changjiahong.banker.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.changjiahong.banker.pdfutils.PDFViewer


@Composable
fun CreditCardTempApp() {

    Column(modifier = Modifier.background(Color.LightGray)) {

        PDFViewer(
            "/Volumes/Ti600/Users/changjiahong/Documents/李二庄/3《个人征信业务授权书》.pdf",
            modifier = Modifier.width(300.dp)
        )
    }
}
