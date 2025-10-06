package cn.changjiahong.banker.repository

import cn.changjiahong.banker.SelectByBid
import cn.changjiahong.banker.UIMeta
import cn.changjiahong.banker.model.Meta

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

    fun findUIMetasByLabel(label: String): List<UIMeta>

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
    fun findUIMetasByBid(bid: Long): List<SelectByBid>
    fun newMetaValue(uId: Long,bid: Long, metaId: Long, metaValue: String): Long
    fun updateMetaValueById(metaValueId: Long, metaValue: String)

    fun findMetasByUidAndBid(uid: Long, bid: Long): List<Meta>


}