package cn.changjiahong.banker.app.about.settings.template

import cafe.adriel.voyager.core.model.screenModelScope
import cn.changjiahong.banker.Template
import cn.changjiahong.banker.app.RR
import cn.changjiahong.banker.storage.FileType
import cn.changjiahong.banker.mvi.MviScreenModel
import cn.changjiahong.banker.mvi.UiEffect
import cn.changjiahong.banker.mvi.UiEvent
import cn.changjiahong.banker.service.TemplateService
import cn.changjiahong.banker.storage.Storage
import cn.changjiahong.banker.storage.containsFile
import cn.changjiahong.banker.uieffect.GoEffect
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.isDirectory
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.nameWithoutExtension
import io.github.vinceglb.filekit.path
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

sealed interface TempSettingUiEvent : UiEvent {
    class GoPreTemplateScreen(val template: Template) : TempSettingUiEvent
    class GoTempFiledSettingScreen(val template: Template) : TempSettingUiEvent

    class AddDocTemplate(val file: PlatformFile) : TempSettingUiEvent

}

sealed interface TempSettingUiEffect : UiEffect {

    object AddTempSuccess : TempSettingUiEffect
}

@Factory
class TemplateSettingScreenModel(val templateService: TemplateService) : MviScreenModel() {

    private val _tempFiles = MutableStateFlow<List<Template>>(emptyList())

    val tempFiles = _tempFiles.asStateFlow()

    override fun handleEvent(event: UiEvent) {
        when (event) {
            is TempSettingUiEvent.GoPreTemplateScreen -> GoEffect(RR.PRE_TEMPLATE(event.template)).trigger()
            is TempSettingUiEvent.GoTempFiledSettingScreen -> GoEffect(RR.TEMP_FIELD_SETTING(event.template)).trigger()

            is TempSettingUiEvent.AddDocTemplate -> addDocTemplate(event.file)
        }
    }

    private fun addDocTemplate(file: PlatformFile) {
        if (!file.exists()) {
            toast("选择的模版文件不存在！")
            return
        }
        if (file.isDirectory()) {
            toast("不允许选择文件夹")
            return
        }
        val fileType = FileType.getFileType(file.extension)
        if (!FileType.supported(fileType)) {
            toast("不受支持的文件类型")
            return
        }
        val fileName = file.name

        screenModelScope.launch {
            var newFile: PlatformFile

            if (!Storage.templatesDir.containsFile(fileName)) {
                newFile = Storage.getTemplatesFile(fileName)
                file.copyTo(newFile)
            } else {
                toast("已存在同名文件！！")
                return@launch
            }

            templateService.addNewTemplate(newFile.path, newFile.nameWithoutExtension, fileType)
                .catchAndCollect {
                    toast("添加成功")
                    TempSettingUiEffect.AddTempSuccess.trigger()
                }
        }
    }

    init {
        loadTemplates()
    }

    private fun loadTemplates() {
        screenModelScope.launch {

            templateService.getAllDocTemps().collect {
                _tempFiles.value = it
            }

        }
    }

}