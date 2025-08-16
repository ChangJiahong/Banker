package cn.changjiahong.banker.app.business_handle

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BizField
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.model.BusinessRelated
import cn.changjiahong.banker.model.UserDO
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UserService
import cn.changjiahong.banker.uieffect.GoDIREffect
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
    val templateService: TemplateService
) : MviScreenModel() {
    private val _clientelesData = MutableStateFlow<List<UserDO>>(listOf())
    val clientelesData = _clientelesData.asStateFlow()


    /**
     * 基本信息
     */
    private val _basicFields = MutableStateFlow<List<BasicField>>(listOf())
    val basicFields = _basicFields.asStateFlow()

    private val _templatesData = MutableStateFlow<List<Template>>(listOf())
    val templatesData = _templatesData.asStateFlow()

    private val _basicFieldValues = MutableStateFlow<List<String>>(emptyList())

    /**
     * 业务信息
     */
    private val _bizFields = MutableStateFlow<List<BizField>>(emptyList())
    val businessFields = _bizFields.asStateFlow()

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

            is BhUIEvent.SelectedClientele -> {
                _currentlySelected.value = event.userDO
            }

            is BhUIEvent.GoPreTemplate -> {
                GoDIREffect(RR.TEMPLATE(event.businessId, event.template, event.userId)).trigger()
            }
        }
    }


    init {
        loadClientele()
        loadDocTemplates()
        loadBasicFields()
        loadBusinessTypeFields()
    }

    private fun loadDocTemplates() {
        screenModelScope.launch {
            templateService.getDocTempsByBusinessId(business.id).collect {
                _templatesData.value = it
            }
        }
    }


    /**
     * 用户基本信息
     */
    fun loadClientele() {
        screenModelScope.launch {
//            userService.getUsersByBR(BusinessRelated.EPay).collect {
//                _clientelesData.value = it
//            }
        }
    }


    fun loadBasicFields() {
        screenModelScope.launch {
            userService.getUserFieldsByBusinessId(business.id).collect {
                _basicFields.value = it
            }
        }
    }

    /**
     * 初始化业务属性信息
     */
    fun loadBusinessTypeFields(call: () -> Unit = {}) {
        screenModelScope.launch {
            businessService.getFieldsByBusinessId(business.id).collect { businessFields ->

                println(business.id)
                println(businessFields)
                _bizFields.value = businessFields


//                val fieldValues = mutableMapOf<Long, String>()
//                val fieldErrorMsg = mutableMapOf<String, String>()
//                businessFields.forEach { field ->
//                    fieldValues += field.associate {
//                        it.id to ""
//                    }
//                    fieldErrorMsg += fields.associate {
//                        it.fieldName to ""
//                    }
//                }
//
//                _uiState.update {
//                    it.copy(
//                        businessFields = businessFields,
//                        fieldValues = fieldValues,
//                        fieldErrorMsg = fieldErrorMsg
//                    )
//                }
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