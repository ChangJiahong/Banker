package cn.changjiahong.banker.app.business_handle

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.app.xinef.EPayUIEvent
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class BusinessHandlerScreenModel(
    val business: Business,
    val userService: UserService, val businessService: BusinessService,
) : MviScreenModel() {
    private val _clientelesData = MutableStateFlow<List<UserDO>>(listOf())
    val clientelesData = _clientelesData.asStateFlow()

    private val _currentlySelected = MutableStateFlow<UserDO?>(null)
    val currentlySelected = _currentlySelected.asStateFlow()

    private val _uiState by lazy { MutableStateFlow(BhUiState()) }
    val uiState = _uiState.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is BhUIEvent.AddClientele -> {
                BhEffect.OpenDialog.trigger()
            }

            is BhUIEvent.EnterField -> {
                _uiState.value = _uiState.value.copy(
                    fieldValues = _uiState.value.fieldValues + (event.fieldId to event.fieldValue),
                )
            }

            is BhUIEvent.EnterUsername -> {
                _uiState.update { it.copy(username = event.username) }
            }

            is BhUIEvent.EnterIdNum -> {
                _uiState.update { it.copy(idNumber = event.idNum) }
            }

            is BhUIEvent.EnterPhone -> {
                _uiState.update { it.copy(phone = event.phone) }
            }

            is BhUIEvent.SaveBhDetail -> {
                saveBhDetail()
            }
        }
    }


    init {
        loadClientele()
        loadBusinessTypeFields()
    }


    fun loadClientele() {
        screenModelScope.launch {
            userService.getUsersByBR(BusinessRelated.EPay).collect {
                _clientelesData.value = it
            }
        }
    }

    fun loadBusinessTypeFields(call: () -> Unit = {}) {
        screenModelScope.launch {
            businessService.getFieldsByBusinessId(business.id).collect { businessFields ->
                val fieldValues = mutableMapOf<Long, String>()
                val fieldErrorMsg = mutableMapOf<String, String>()
                businessFields.fieldGroups.forEach { (groupId, groupName, fields) ->
                    fieldValues += fields.associate {
                        it.id to ""
                    }
                    fieldErrorMsg += fields.associate {
                        it.fieldName to ""
                    }
                }

                _uiState.update {
                    it.copy(
                        businessFields = businessFields,
                        fieldValues = fieldValues,
                        fieldErrorMsg = fieldErrorMsg
                    )
                }
                call()
            }
        }
    }

    fun saveBhDetail() {
//        if (!checkUsername() || !checkIdNum() || !checkPhone()
//            || !checkBAddress() || !checkBScope() || !checkBankerNum()
//        ) {
//            return
//        }

        val state = uiState.value

        screenModelScope.launch {

            businessService.saveBusinessWithUserValue(
                state.username,
                state.idNumber,
                state.phone,
                business.id,
                state.fieldValues,
            ).catch { e ->
                e.printStackTrace()
//                _uiState.update { it.copy(usernameError = e.message ?: "") }
            }.collect {
                BhEffect.CloseDialog.trigger()
            }
//            ePayService.saveEPay(
//                username = state.username,
//                idNum = state.idNumber,
//                phone = state.phone,
//                bAddress = state.bAddress,
//                bScope = state.bScope,
//                bankerNum = state.bankerNum
//            ).catch { e ->
//                _uiState.update { it.copy(usernameError = e.message ?: "") }
//            }.collect {
//                EPayEffect.CloseDialog.trigger()
//            }
        }


    }
}