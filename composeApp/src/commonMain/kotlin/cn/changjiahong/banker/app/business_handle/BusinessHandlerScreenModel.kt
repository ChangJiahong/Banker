package cn.changjiahong.banker.app.business_handle

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.FieldConfig
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.model.Field
import cn.changjiahong.banker.model.FieldVal
import cn.changjiahong.banker.model.TemplateFillerItem
import cn.changjiahong.banker.model.UserInfo
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UserService
import cn.changjiahong.banker.uieffect.GoDIREffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class BusinessHandlerScreenModel(
    val business: Business,
    val userService: UserService,
    val fieldService: FieldService,
    val businessService: BusinessService,
    val templateService: TemplateService
) : MviScreenModel() {
    private val _clientelesData = MutableStateFlow<List<UserInfo>>(listOf())
    val clientelesData = _clientelesData.asStateFlow()


    /**
     * 基本信息
     */
    private val _basicFields = MutableStateFlow<List<FieldConfig>>(listOf())
    val basicFields = _basicFields.asStateFlow()

    private val _templatesData = MutableStateFlow<List<Template>>(listOf())
    val templatesData = _templatesData.asStateFlow()

    private val _fieldValues = MutableStateFlow<Map<Long, FieldVal>>(emptyMap())
    val fieldValues = _fieldValues.asStateFlow()

    /**
     * 业务信息
     */
    private val _bizFields = MutableStateFlow<List<FieldConfig>>(emptyList())
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

            }
        }
    }


    init {
        loadClientele()
        loadTemplates()
        loadFieldConfigs()
    }

    private fun loadTemplates() {
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
            userService.getUserInfos(business.id).catchAndCollect {
                _clientelesData.value = it
            }
        }
    }


    fun loadFieldConfigs() {
        screenModelScope.launch {
            fieldService.getFieldConfigsForBiz(business.id).catchAndCollect { data ->
                _basicFields.value = data.filter { f -> f.bId == -1L }

                _bizFields.value = data.filterNot { f -> f.bId == -1L }
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

            fieldService.saveFieldValues(
                currentlySelected.value?.uid,
                business.id, _fieldValues.value.values.toList()
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

        _fieldValues.value = currentUser.fields.values.associate {
            it.fieldId to FieldVal(
                it.fieldId,
                it.fieldValueId,
                it.fieldValue
            )
        }

        clienteleDialog.show()
    }

    fun clickTplItem(template: Template) {
        if (currentlySelected.value == null || currentlySelected.value!!.uid < 0) {
            tip("请先勾选一个客户")
            return
        }
        val user = currentlySelected.value!!

        screenModelScope.launch {

            fieldService.getFieldConfigsForTemplate(business.id, template.id)
                .catchAndCollect { data ->
                    val fields = user.fields.values



                    data.forEach { f ->
                        if (user.fields.values.none { it.fieldId == f.fieldId && it.fieldValue.isNotBlank() }) {
                            tip("信息不完善")
                            return@catchAndCollect
                        }
                    }

                    val fillData = data.map { f ->
                        fields.first { it.fieldId == f.fieldId && it.fieldValue.isNotBlank() }
                    }
                    //信息完善

                    GoDIREffect(
                        RR.TEMPLATE(
                            user.uid,
                            business.id,
                            template,
                            fillData
                        )
                    ).trigger()


                }
        }

    }
}