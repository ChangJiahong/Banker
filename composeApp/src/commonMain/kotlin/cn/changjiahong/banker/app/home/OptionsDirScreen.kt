package cn.changjiahong.banker.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import banker.composeapp.generated.resources.Res
import banker.composeapp.generated.resources.dir
import cafe.adriel.voyager.koin.koinScreenModel
import cn.changjiahong.banker.FoldersButton
import cn.changjiahong.banker.app.DirScreen
import org.jetbrains.compose.resources.painterResource


object OptionsDirScreen : DirScreen {

    override val dirName: String
        get() = "首页"

    @Composable
    override fun Content() {
        val dirScreenModel = koinScreenModel<DirScreenModel>()

        val businessList by dirScreenModel.businessList.collectAsState()

        LazyVerticalGrid(
            GridCells.FixedSize(100.dp), contentPadding = PaddingValues(5.dp),
            horizontalArrangement = Arrangement.Center, verticalArrangement = Arrangement.Center
        ) {
            itemsIndexed(businessList) { index, item ->
                FoldersButton(item.businessName) {
                    DirUiEvent.ClickBusiness(item).sendTo(dirScreenModel)
                }
            }
        }

    }
}

