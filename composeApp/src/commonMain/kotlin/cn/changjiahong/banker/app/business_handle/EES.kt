package cn.changjiahong.banker.app.business_handle

import androidx.compose.runtime.Stable
import cn.changjiahong.banker.BizField
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.UiState

@Stable
data class BhUiState(
    val bizFields: List<BizField> = emptyList(),

    val fieldValues: Map<Long, String> = emptyMap(),
    val fieldErrorMsg: Map<String, String> = emptyMap(),

    ) : UiState

sealed interface BhUIEvent : UiEvent {
    object AddClientele : BhUIEvent
    object SaveBhDetail : BhUIEvent

    data class EnterField(val fieldId: Long, val fieldValue: String): BhUIEvent

    data class SelectedClientele(val userDO: UserDO) : BhUIEvent

    data class GoPreTemplate(val businessId: Long, val template: Template, val userId: Long) : BhUIEvent

}

sealed interface BhEffect : UiEffect {
    object CloseDialog : BhEffect
    object OpenDialog : BhEffect
}
