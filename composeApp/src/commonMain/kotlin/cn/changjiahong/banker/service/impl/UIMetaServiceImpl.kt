package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.model.UIMetaConfig
import cn.changjiahong.banker.repository.UIMetaRepository
import cn.changjiahong.banker.service.UIMetaService
import cn.changjiahong.banker.utils.FlowList
import cn.changjiahong.banker.utils.OkFlow
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import org.koin.core.annotation.Factory

/**
 *
 * @author ChangJiahong
 * @date 2025/9/9
 */

@Factory
class UIMetaServiceImpl(
    val db: BankerDb,
    val uiMetaRepository: UIMetaRepository
) : UIMetaService {

    override fun getUIMetaConfigs() = returnFlow {
        uiMetaRepository.findUIMetas().map { meta -> UIMetaConfig(meta) }
    }

    override fun getUIMetaConfigsByLabel(label: String) = returnFlow {
        uiMetaRepository.findUIMetasByLabel(label).map { meta -> UIMetaConfig(meta) }
    }

    override fun saveUIMetaConfigs(uiMetaConfigs: List<UIMetaConfig>) = okFlow {
        db.transaction {
            uiMetaConfigs.forEach { fieldConfig ->
                fieldConfig.run {
                    if (metaId < 0 && !isDelete) {
                        uiMetaRepository.newUIMeta(
                            label,
                            metaType,
                            width,
                            options,
                            validation,
                            forced,
                            isGlobal
                        )
                    } else if (!fieldConfig.isDelete) {
                        uiMetaRepository.updateUIMetaById(
                            label,
                            metaType,
                            width,
                            options,
                            validation,
                            forced,
                            isGlobal,
                            metaId
                        )
                    } else {
                        uiMetaRepository.deleteUIMetaById(metaId)
                    }
                }
            }
        }

    }
}