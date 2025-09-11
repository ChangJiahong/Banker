package cn.changjiahong.banker.service

import cn.changjiahong.banker.model.UIMetaConfig
import cn.changjiahong.banker.utils.FlowList
import cn.changjiahong.banker.utils.OkFlow
import kotlinx.coroutines.flow.Flow

/**
 *
 * @author ChangJiahong
 * @date 2025/9/9
 */

interface UIMetaService{

    /**
     * 获取UI元数据配置
     */
    fun getUIMetaConfigs(): FlowList<UIMetaConfig>

    fun getUIMetaConfigsByLabel(label: String): FlowList<UIMetaConfig>

    /**
     * 保存UI元数据配置
     */
    fun saveUIMetaConfigs(uiMetaConfigs: List<UIMetaConfig>): OkFlow


}
