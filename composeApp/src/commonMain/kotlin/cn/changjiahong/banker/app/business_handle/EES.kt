package cn.changjiahong.banker.app.business_handle

import androidx.compose.runtime.Stable
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.model.Field
import cn.changjiahong.banker.model.FieldVal
import cn.changjiahong.banker.model.UserInfo
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.UiState

@Stable
data class BhUiState(

    val fieldValues: Map<Long, String> = emptyMap(),
    val fieldErrorMsg: Map<String, String> = emptyMap(),

    ) : UiState

sealed interface BhUIEvent : UiEvent {
    object NewClientele : BhUIEvent
    object EditClientele : BhUIEvent
    object SaveBhDetail : BhUIEvent

    object SystemOpenFile : BhUIEvent

    data class UpdateFieldValue(val fieldId: Long, val fieldValue: FieldVal) : BhUIEvent

    data class SelectedClientele(val user: UserInfo) : BhUIEvent

    data class ClickTplItem(val template: Template) : BhUIEvent

    data class PrintTplItem(val template: Template) : BhUIEvent

    data class OtherOpenTplItem(val template: Template) : BhUIEvent


    data class UpdateOptionV(val filedId:Long,val index:Int,val ov: Map<String, String>): BhUIEvent
    data class AddOptionV(val filedId:Long,val options: String): BhUIEvent

}

