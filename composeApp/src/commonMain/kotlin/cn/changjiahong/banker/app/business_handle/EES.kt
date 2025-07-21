package cn.changjiahong.banker.app.business_handle

import androidx.compose.runtime.Stable
import cn.changjiahong.banker.DocTemplate
import cn.changjiahong.banker.model.BusinessFields
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.UiState

@Stable
data class BhUiState(
    val businessFields: BusinessFields = BusinessFields(),

    val fieldValues: Map<Long, String> = emptyMap(),
    val fieldErrorMsg: Map<String, String> = emptyMap(),

    val username: String = "",
    val idNumber: String = "",
    val phone: String = "",

    val usernameError: String = "",
    val idNumberError: String = "",
    val phoneError: String = "",

) : UiState

sealed interface BhUIEvent : UiEvent {
    object AddClientele : BhUIEvent
    object SaveBhDetail : BhUIEvent

    data class EnterField(val fieldId: Long, val fieldValue: String): BhUIEvent

    data class EnterUsername(val username: String) : BhUIEvent
    data class EnterIdNum(val idNum: String) : BhUIEvent
    data class EnterPhone(val phone: String) : BhUIEvent
    data class EnterBAddress(val bAddress: String) : BhUIEvent
    data class EnterBScope(val bScope: String) : BhUIEvent
    data class EnterBankerNum(val bankerNum: String) : BhUIEvent

    data class SelectedClientele(val userDO: UserDO) : BhUIEvent

    data class GoPreTemplate(val businessId: Long,val template: DocTemplate,val userId: Long) : BhUIEvent

}

sealed interface BhEffect : UiEffect {
    object CloseDialog : BhEffect
    object OpenDialog : BhEffect
}
