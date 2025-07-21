package cn.changjiahong.banker.app.xinef

import androidx.compose.runtime.Stable
import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.UiState
import cn.changjiahong.banker.service.EPayService
import cn.changjiahong.banker.service.UserService
import cn.changjiahong.banker.uieffect.GoDIREffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.koin.core.annotation.Factory
import kotlin.getValue

@Stable
data class EPayUiState(
    val username: String = "",
    val idNumber: String = "",
    val phone: String = "",
    val bAddress: String = "",
    val bScope: String = "",
    val bankerNum: String = "",

    val usernameError: String = "",
    val idNumberError: String = "",
    val phoneError: String = "",
    val bAddressError: String = "",
    val bScopeError: String = "",
    val bankerNumError: String = "",
) : UiState

sealed interface EPayUIEvent : UiEvent {
    object AddClientele : EPayUIEvent
    object SaveEPayDetail : EPayUIEvent


    data class EnterUsername(val username: String) : EPayUIEvent
    data class EnterIdNum(val idNum: String) : EPayUIEvent
    data class EnterPhone(val phone: String) : EPayUIEvent
    data class EnterBAddress(val bAddress: String) : EPayUIEvent
    data class EnterBScope(val bScope: String) : EPayUIEvent
    data class EnterBankerNum(val bankerNum: String) : EPayUIEvent

    data class SelectedClientele(val userDO: UserDO) : EPayUIEvent

    data class GoPreTemplate(val name: String) : EPayUIEvent

}

sealed interface EPayEffect : UiEffect {
    object CloseDialog : EPayEffect
}

@Factory
class EPayScreenModel(val userService: UserService, val ePayService: EPayService) :
    MviScreenModel() {

    private val _clientelesData = MutableStateFlow<List<UserDO>>(listOf())
    val clientelesData = _clientelesData.asStateFlow()

    private val _currentlySelected = MutableStateFlow<UserDO?>(null)
    val currentlySelected = _currentlySelected.asStateFlow()

    private val _uiState by lazy { MutableStateFlow(EPayUiState()) }
    val uiState = _uiState.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            EPayUIEvent.AddClientele -> {
                val ol = mutableListOf<UserDO>()
                ol.addAll(_clientelesData.value)
                ol.add(
                    UserDO(
                        2,
                        "张三2",
                        "123213",
                        created = Clock.System.now(),
                        phone = "",
                        businessRelated = BusinessRelated.EPay
                    )
                )
                _clientelesData.value = ol
            }

            is EPayUIEvent.EnterUsername -> {
                _uiState.update { it.copy(username = event.username) }
            }

            is EPayUIEvent.EnterIdNum -> {
                _uiState.update { it.copy(idNumber = event.idNum) }
            }

            is EPayUIEvent.EnterPhone -> {
                _uiState.update { it.copy(phone = event.phone) }
            }

            is EPayUIEvent.EnterBAddress -> {
                _uiState.update { it.copy(bAddress = event.bAddress) }
            }

            is EPayUIEvent.EnterBScope -> {
                _uiState.update { it.copy(bScope = event.bScope) }
            }

            is EPayUIEvent.EnterBankerNum -> {
                _uiState.update { it.copy(bankerNum = event.bankerNum) }
            }

            is EPayUIEvent.SelectedClientele -> {
                _currentlySelected.value = event.userDO
            }

            is EPayUIEvent.GoPreTemplate ->{
//                GoDIREffect(RR.TEMPLATE).trigger()
            }

            EPayUIEvent.SaveEPayDetail -> {
                saveEPayDetail()
            }
        }
    }


    init {
        loadClientele()
    }


    fun loadClientele() {
        screenModelScope.launch {
            userService.getUsersByBR(BusinessRelated.EPay).collect {
                _clientelesData.value = it
            }
        }
    }

    fun saveEPayDetail() {
        if (!checkUsername() || !checkIdNum() || !checkPhone()
            || !checkBAddress() || !checkBScope() || !checkBankerNum()
        ) {
            return
        }

        val state = uiState.value

        screenModelScope.launch {
            ePayService.saveEPay(
                username = state.username,
                idNum = state.idNumber,
                phone = state.phone,
                bAddress = state.bAddress,
                bScope = state.bScope,
                bankerNum = state.bankerNum
            ).catch { e ->
                _uiState.update { it.copy(usernameError = e.message ?: "") }
            }.collect {
                EPayEffect.CloseDialog.trigger()
            }
        }


    }


    fun checkUsername(): Boolean {
        val username = uiState.value.username
        var msg = ""
        if (username.isEmpty()) {
            msg = "不能为空"
        }
        _uiState.update { it.copy(usernameError = msg) }
        return msg.isEmpty()
    }

    fun checkIdNum(): Boolean {
        val value = uiState.value.idNumber
        var msg = ""
        if (value.isEmpty()) {
            msg = "不能为空"
        }
        _uiState.update { it.copy(idNumberError = msg) }
        return msg.isEmpty()
    }

    fun checkPhone(): Boolean {
        val value = uiState.value.phone
        var msg = ""
        if (value.isEmpty()) {
            msg = "不能为空"
        }
        _uiState.update { it.copy(phoneError = msg) }
        return msg.isEmpty()
    }

    fun checkBAddress(): Boolean {
        val value = uiState.value.bAddress
        var msg = ""
        if (value.isEmpty()) {
            msg = "不能为空"
        }
        _uiState.update { it.copy(bAddressError = msg) }
        return msg.isEmpty()
    }

    fun checkBScope(): Boolean {
        val value = uiState.value.bScope
        var msg = ""
        if (value.isEmpty()) {
            msg = "不能为空"
        }
        _uiState.update { it.copy(bScopeError = msg) }
        return msg.isEmpty()
    }

    fun checkBankerNum(): Boolean {
        val value = uiState.value.bankerNum
        var msg = ""
        if (value.isEmpty()) {
            msg = "不能为空"
        }
        _uiState.update { it.copy(bankerNumError = msg) }
        return msg.isEmpty()
    }

}