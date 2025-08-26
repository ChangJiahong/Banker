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

    data class UpdateFieldValue(val fieldId: Long, val fieldValue: FieldVal): BhUIEvent

    data class SelectedClientele(val user: UserInfo) : BhUIEvent

    data class ClickTplItem(val template: Template) : BhUIEvent

}

sealed interface BhEffect : UiEffect {
    object CloseDialog : BhEffect
    object OpenDialog : BhEffect
}
