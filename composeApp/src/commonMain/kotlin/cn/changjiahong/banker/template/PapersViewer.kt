package cn.changjiahong.banker.pdfutils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources._1
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.max

data class Paper(val painter: Painter?)

@Composable
fun PapersViewer(papers: List<Paper>, modifier: Modifier = Modifier) {
    var scale by remember { mutableFloatStateOf(1f) }

    val transformState = rememberTransformableState { zoomChange, pan, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
    }

    Papers(
        papers, modifier = modifier
            .transformable(transformState)
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 5f)
                }
            }, scale
    )

}

@Composable
private fun Papers(papers: List<Paper>, modifier: Modifier = Modifier, scale: Float = 1f) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
    ) {
        val scrollStateX = rememberScrollState()

        val maxWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        LazyColumn(
            modifier = Modifier.width(maxWidth).horizontalScroll(scrollStateX),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(papers.size) { index ->
                val page = papers[index]
                Box(
                    modifier = Modifier.width(maxWidth * max(scale, 1f)),
                    contentAlignment = Alignment.TopCenter
                ) {

                    Box(modifier = Modifier.width(maxWidth * scale)) {

                        if (page.painter != null) {
                            Image(
                                painter = page.painter, contentDescription = "Page $index",
                                modifier = Modifier
                                    .fillMaxWidth(), contentScale = ContentScale.Fit
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(Color.White)) {
                                    Text("Loading Page$index")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreViewPaperViewer() {
    PapersViewer(
        listOf(
            Paper(painterResource(Res.drawable._1)),
            Paper(painterResource(Res.drawable._1)),
            Paper(painterResource(Res.drawable._1)),
            Paper(painterResource(Res.drawable._1)),
        )
    )
}
