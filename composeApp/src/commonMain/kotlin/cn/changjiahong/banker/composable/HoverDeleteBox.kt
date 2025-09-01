package cn.changjiahong.banker.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.remove
import cn.changjiahong.banker.utils.padding
import org.jetbrains.compose.resources.painterResource

@Composable
fun HoverDeleteBox(text: String, modifier: Modifier, onDeleteClick: () -> Unit = {}) {
//    val interactionSource = remember { MutableInteractionSource() }
//    val isHovered by interactionSource.collectIsHoveredAsState()
//
//    Box(
//        modifier.size(35.dp).background(
//            color = if (isHovered) Color(0xffEA3323)
//            else Color(91, 165, 243),
//            shape = RoundedCornerShape(6.dp) // 圆角 16dp
//        ).hoverable(interactionSource = interactionSource) // 支持 hover
//        , contentAlignment = Alignment.Center
//    ) {
//        if (isHovered) {
//            IconButton(onClick = onDeleteClick) {
//                Icon(
//                    painterResource(Res.drawable.remove),
//                    contentDescription = "删除",
//                    tint = Color.White
//                )
//            }
//        } else {
//            Text(text, fontSize = 18.sp, color = Color.White)
//        }
//    }


    HoverBox(modifier.size(35.dp), hovered = {
        Box(
            Modifier.fillMaxSize().background(
                color = Color(0xffEA3323),
                shape = RoundedCornerShape(6.dp) // 圆角 16dp
            )
        ) {
            IconButton(onClick = onDeleteClick) {
                Icon(
                    painterResource(Res.drawable.remove),
                    contentDescription = "删除",
                    tint = Color.White
                )
            }
        }
    }) {
        Box(
            Modifier.fillMaxSize().background(
                color = Color(91, 165, 243),
                shape = RoundedCornerShape(6.dp) // 圆角 16dp
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontSize = 18.sp, color = Color.White)
        }
    }
}


@Composable
fun HoverBox(
    modifier: Modifier = Modifier,
    hovered: @Composable BoxScope.() -> Unit,
    default: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier.hoverable(interactionSource = interactionSource) // 支持 hover
        , contentAlignment = Alignment.Center
    ) {
        if (isHovered) {
            hovered()
        } else {
            default()
        }
    }
}