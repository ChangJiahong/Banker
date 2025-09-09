package cn.changjiahong.banker.repository

import cn.changjiahong.banker.UIMeta

/**
 *
 * @author ChangJiahong
 * @date 2025/9/9
 */

interface UIMetaRepository {

    /**
     * 查找
     */
    fun findUIMetas(): List<UIMeta>

    fun newUIMeta(
        label: String,
        metaType: String,
        width: Int,
        options: String,
        validation: String,
        forced: Boolean,
        global: Boolean
    ): Long

    fun updateUIMetaById(
        label: String,
        metaType: String,
        width: Int,
        options: String,
        validation: String,
        forced: Boolean,
        global: Boolean,
        metaId: Long
    )

    fun deleteUIMetaById(metaId: Long)


}