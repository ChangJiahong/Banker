package cn.changjiahong.banker.model

import cn.changjiahong.banker.UIMeta
import cn.changjiahong.banker.utils.toBoolean

/**
 *
 * @author ChangJiahong
 * @date 2025/9/9
 */

data class UIMetaConfig(
    val metaId: Long = -1,
    val label: String = "",
    val metaType: String = "TEXT",
    val width: Int = 20,
    val options: String = "",
    val validation: String = "",
    val forced: Boolean = false,
    val isGlobal: Boolean = false,
    val isDelete: Boolean = false
) {
    constructor(meta: UIMeta) : this(
        metaId = meta.metaId,
        label = meta.label,
        metaType = meta.metaType,
        width = meta.width.toInt(),
        options = meta.options ?: "",
        validation = meta.validation,
        forced = meta.forced.toBoolean(),
        isGlobal = meta.isGlobal.toBoolean()
    )

    data class Error(
        val label: String = "",
        val width: String = "",
        val options: String = "",
        val validation: String = "",
    )
}