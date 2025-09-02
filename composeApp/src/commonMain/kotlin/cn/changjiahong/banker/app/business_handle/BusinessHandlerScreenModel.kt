package cn.changjiahong.banker.app.business_handle

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.FieldConfig
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.composable.AlertDialogState
import cn.changjiahong.banker.composable.DialogState
import cn.changjiahong.banker.model.FieldVal
import cn.changjiahong.banker.model.Table
import cn.changjiahong.banker.model.UserInfo
import cn.changjiahong.banker.model.isTableType
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.mvi.replace
import cn.changjiahong.banker.platform.SystemPrinter
import cn.changjiahong.banker.platform.systemOpen
import cn.changjiahong.banker.service.FieldService
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.service.UserService
import cn.changjiahong.banker.storage.FileType
import cn.changjiahong.banker.storage.FileType.DOC
import cn.changjiahong.banker.storage.FileType.DOCX
import cn.changjiahong.banker.storage.FileType.PDF
import cn.changjiahong.banker.storage.FileType.XLS
import cn.changjiahong.banker.storage.FileType.XLSX
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

    private val _optionsKey = MutableStateFlow<Map<Long, List<String>>>(emptyMap())
    val optionsKey = _optionsKey.asStateFlow()

    private val _optionsFields =
        MutableStateFlow<Map<Long, Table>>(emptyMap())
    val optionsFields = _optionsFields.asStateFlow()


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
    val openOtherSoftwareDialog = AlertDialogState()

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

            is BhUIEvent.PrintTplItem -> {
                if (currentlySelected.value == null || currentlySelected.value!!.uid < 0) {
                    tip("请先勾选一个客户")
                    return
                }
                val user = currentlySelected.value!!

                screenModelScope.launch {

                    templateService.fillFromToTemplate(user.uid, business.id, event.template)
                        .catchAndCollect {
                            SystemPrinter.print(it).catchAndCollect { toast("ok") }
                        }
                }

            }

            is BhUIEvent.OtherOpenTplItem -> {
                if (currentlySelected.value == null || currentlySelected.value!!.uid < 0) {
                    tip("请先勾选一个客户")
                    return
                }
                val user = currentlySelected.value!!

                screenModelScope.launch {

                    templateService.fillFromToTemplate(user.uid, business.id, event.template)
                        .catchAndCollect {
                            systemOpen(it)
                        }
                }
            }

            is BhUIEvent.UpdateOptionV -> {
                _optionsFields.replace(event.filedId) {
                    event.ov
                }

            }

            is BhUIEvent.AddOptionV -> {
                _optionsFields.replace(event.filedId) {
                    _optionsFields.value[event.filedId]!!.copy {
                        createRow()
                    }
                }

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

                val tableFields = data.filter { f ->
                    f.fieldType.isTableType()
                }

                _optionsKey.value =
                    tableFields.associate { it.fieldId to (it.options ?: "").split(",") }

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

            val fv = _fieldValues.value.toMutableMap()


            _optionsFields.value.forEach { (key, value) ->
                fv[key] = FieldVal(value.fieldIds, value.fieldValueId, value.toFieldValue())
            }

            fieldService.saveFieldValues(
                currentlySelected.value?.uid,
                business.id, fv.values.toList()
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

        _optionsFields.value = emptyMap()


        clienteleDialog.show()

    }

    fun editClientele() {
        if (_currentlySelected.value == null) {
            tip("请勾选一个客户")
            return
        }

        val currentUser = currentlySelected.value!!

        _optionsFields.value =
            currentUser.fields.values.filter { it.fieldType.isTableType() }.associate {
                it.fieldId to Table(
                    optionsKey.value[it.fieldId]!!,
                    it.fieldId,
                    it.fieldValueId,
                    it.fieldValue
                )
            }

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

            templateService.fillFromToTemplate(user.uid, business.id, template).catchAndCollect {
                when (FileType.getFileType(template.fileType)) {
                    PDF -> GoDIREffect(RR.DIR_PRE_TEMPLATE(it)).trigger()
                    DOC, DOCX, XLS, XLSX -> {
                        openOtherSoftwareDialog.show() {
                            systemOpen(it)
                            openOtherSoftwareDialog.dismiss()
                        }
                    }
                }
            }
        }

    }
}