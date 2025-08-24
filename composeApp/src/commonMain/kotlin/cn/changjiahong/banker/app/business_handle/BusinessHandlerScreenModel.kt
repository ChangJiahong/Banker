package cn.changjiahong.banker.app.business_handle

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.BizField
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.BasicField
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.model.FieldValue
import cn.changjiahong.banker.model.UserInfo
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class BusinessHandlerScreenModel(
    val business: Business,
    val userService: UserService, val businessService: BusinessService,
    val templateService: TemplateService
) : MviScreenModel() {
    private val _clientelesData = MutableStateFlow<List<UserInfo>>(listOf())
    val clientelesData = _clientelesData.asStateFlow()


    /**
     * 基本信息
     */
    private val _basicFields = MutableStateFlow<List<BasicField>>(listOf())
    val basicFields = _basicFields.asStateFlow()

    private val _templatesData = MutableStateFlow<List<Template>>(listOf())
    val templatesData = _templatesData.asStateFlow()

    private val _fieldValues = MutableStateFlow<Map<Long, FieldValue>>(emptyMap())
    val fieldValues = _fieldValues.asStateFlow()

    /**
     * 业务信息
     */
    private val _bizFields = MutableStateFlow<List<BizField>>(emptyList())
    val businessFields = _bizFields.asStateFlow()

    private val _currentlySelected = MutableStateFlow<UserInfo?>(null)
    val currentlySelected = _currentlySelected.asStateFlow()

    private val _uiState by lazy { MutableStateFlow(BhUiState()) }
    val uiState = _uiState.asStateFlow()

    val clienteleDialog = DialogState()


    override fun handleEvent(event: UiEvent) {
        when (event) {
            is BhUIEvent.NewClientele -> newClientele()

            is BhUIEvent.EditClientele -> editClientele()

            is BhUIEvent.UpdateFieldValue -> {
                _fieldValues.replace(event.fieldId) { event.fieldValue }
            }

            is BhUIEvent.SaveBhDetail -> {
                saveBhDetail()
            }

            is BhUIEvent.SelectedClientele -> {
                _currentlySelected.value = event.user
            }

            is BhUIEvent.ClickTplItem -> {

                clickTplItem(event.template)

//                GoDIREffect(RR.TEMPLATE(event.businessId, event.template, event.userId)).trigger()
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
     * 用户综合信息
     */
    fun loadClientele() {
        screenModelScope.launch {
            userService.getUserInfos().catchAndCollect {
                _clientelesData.value = it
            }
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
    fun loadBusinessTypeFields() {
        screenModelScope.launch {
            businessService.getFieldsByBusinessId(business.id).collect { businessFields ->
                _bizFields.value = businessFields
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

            businessService.saveFields(
                currentlySelected.value?.id,
                business.id,
                _fieldValues.value,
            ).catchAndCollect {
                toast("OK")
                loadClientele()
                clienteleDialog.dismiss()
            }

        }


    }

    private fun newClientele() {

        _fieldValues.value = emptyMap()
        _currentlySelected.value = null

        clienteleDialog.show()

    }

    fun editClientele() {
        if (_currentlySelected.value == null) {
            tip("请勾选一个客户")
            return
        }

        val currentUser = currentlySelected.value!!

        val fvs = mutableMapOf<Long, FieldValue>()

        currentUser.fields.values.forEach { field ->
            fvs[field.fieldId] = FieldValue(
                field.fieldId,
                field.fieldValueId,
                field.fieldValue,
                isBasic = field.isBasic
            )
        }

        _fieldValues.value = fvs

        clienteleDialog.show()
    }

    fun clickTplItem(template: Template){
        if (currentlySelected.value == null || currentlySelected.value!!.id < 0) {
            tip("请先勾选一个客户")
            return
        }
        val user = currentlySelected.value!!

//        event.template
//        templateService.checkTplFillFieldIntegrity(user.id,template.id,business.id)

        // 获取模版所需的属性fieldId
//        templateService.getFillFieldsByTplIdAndBizId(template.id,business.id)

//        basic


//        businessService.getFieldsByBusinessId()
    }
}