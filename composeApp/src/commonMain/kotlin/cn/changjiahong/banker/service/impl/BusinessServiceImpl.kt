package cn.changjiahong.banker.service.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.Business
import cn.changjiahong.banker.model.Biz
import cn.changjiahong.banker.model.FieldOverrideBindingConfig
import cn.changjiahong.banker.model.RelBizUIMetaConfig
import cn.changjiahong.banker.utils.NoData
import cn.changjiahong.banker.repository.BusinessRepository
import cn.changjiahong.banker.repository.TemplateRepository
import cn.changjiahong.banker.service.BusinessService
import cn.changjiahong.banker.utils.FlowList
import cn.changjiahong.banker.utils.OkFlow
import cn.changjiahong.banker.utils.okFlow
import cn.changjiahong.banker.utils.returnFlow
import cn.changjiahong.banker.utils.toId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class BusinessServiceImpl(
    val db: BankerDb,
    val businessRepository: BusinessRepository,
    val templateRepository: TemplateRepository,
) : BusinessService {

    override suspend fun getBusinessList(): Flow<List<Business>> {
        return businessRepository.findBusinessTypes()
    }

    override fun addTemplate(
        businessId: Long,
        templateId: Long
    ): Flow<NoData> = flow {
        businessRepository.insertTemplateIntoBusiness(businessId, templateId)
        emit(NoData)
    }

    override fun removeTemplate(
        bId: Long,
        tid: Long
    ): Flow<NoData> = okFlow {
        businessRepository.deleteTemplateFromBusiness(bId, tid)
    }

    override fun saveBusiness(biz: Biz): Flow<NoData> = okFlow {
        if (biz.id < 0) {
            businessRepository.insertBusiness(biz.name)
        } else {
            businessRepository.updateBusinessById(biz.name, biz.id)
        }
    }

    override fun saveFieldOverrideBindingConfig(configs: List<FieldOverrideBindingConfig>): OkFlow =
        okFlow {
            db.transaction {
                configs.forEachIndexed { index, binding ->
                    binding.run {
                        if (id < 0 && !isDelete) {
                            //new
                            businessRepository.newFieldOverrideBinding(
                                fieldId, metaId.toId(), bId, tId, isFixed, fixedValue
                            )
                        } else if (!isDelete) {
                            businessRepository.updateFieldOverrideBindingById(
                                fieldId, metaId.toId(), isFixed, fixedValue, id
                            )
                        } else {
                            businessRepository.deleteFieldOverrideBindingById(id)

                        }
                    }
                }

            }

        }

    override fun getFieldOverrideBindingConfigs(
        bid: Long,
        tid: Long
    ): FlowList<FieldOverrideBindingConfig> = returnFlow {
        businessRepository.findFieldOverrideBindings(bid, tid).map {
            FieldOverrideBindingConfig(it)
        }
    }

    override fun autoGenerateBizAndUIMetaRelList(bid: Long) = okFlow {
        //获取默认配置，获取覆盖配置，合并配置，比较现有的，生成
        val uiMetaIds = templateRepository.findBizInvolvedUIMeta(bid)
        val uiMetaIds2 = businessRepository.findBizInvolvedUIMeta(bid)
        val uiMetas = uiMetaIds.toMutableSet() + uiMetaIds2
        db.transaction {

            val configs = businessRepository.findBizAndUIMetaRelList(bid)

            configs.forEach { config ->
                if (!uiMetas.contains(config.metaId)) {
                    // 不存在 delete
                    businessRepository.deleteRelBizUIMetaById(config.id)
                }
            }

            uiMetas.forEach { metaId ->
                if (configs.find { it.metaId == metaId } == null) {
                    //不存在 new
                    businessRepository.newRelBizUIMeta(bid, metaId, 0L, "",0)
                }
            }

        }

    }

    override fun getBizAndUIMetaRelList(bid: Long): FlowList<RelBizUIMetaConfig> = returnFlow {
        businessRepository.findBizAndUIMetaRelList(bid)
    }

    override fun saveBizAndUIMetaRelConfigs(configs: List<RelBizUIMetaConfig>): OkFlow = okFlow {
        db.transaction {
            configs.forEachIndexed { index, config ->
                config.run {
                        businessRepository.updateRelBizUIMetaById(
                            weight, tag, tagWeight,id
                        )
                    }
                }
            }
    }
}