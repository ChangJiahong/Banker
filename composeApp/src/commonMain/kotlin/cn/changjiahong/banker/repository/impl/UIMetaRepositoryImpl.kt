package cn.changjiahong.banker.repository.impl

import cn.changjiahong.banker.BankerDb
import cn.changjiahong.banker.SelectByBid
import cn.changjiahong.banker.UIMeta
import cn.changjiahong.banker.ck
import cn.changjiahong.banker.model.Meta
import cn.changjiahong.banker.repository.UIMetaRepository
import cn.changjiahong.banker.utils.getSnowId
import cn.changjiahong.banker.utils.toLong
import org.koin.core.annotation.Factory

/**
 *
 * @author ChangJiahong
 * @date 2025/9/9
 */

@Factory
class UIMetaRepositoryImpl(val db: BankerDb) : UIMetaRepository {
    val uiMetaQueries = db.uIMetaQueries
    val metaValueQueries = db.metaValueQueries

    override fun findUIMetas(): List<UIMeta> {
        return uiMetaQueries.selectAll().executeAsList()
    }

    override fun findUIMetasByLabel(label: String): List<UIMeta> {
        return uiMetaQueries.selectByLabel(label).executeAsList()
    }

    override fun newUIMeta(
        label: String,
        metaType: String,
        width: Int,
        options: String,
        validation: String,
        forced: Boolean,
        global: Boolean
    ): Long {
        val id = getSnowId()
        uiMetaQueries.insert(
            id,
            label,
            metaType,
            width.toLong(),
            options,
            validation,
            forced.toLong(),
            global.toLong()
        ).ck()
        return id
    }

    override fun updateUIMetaById(
        label: String,
        metaType: String,
        width: Int,
        options: String,
        validation: String,
        forced: Boolean,
        global: Boolean,
        metaId: Long
    ) {
        uiMetaQueries.update(
            label,
            metaType,
            width.toLong(),
            options,
            validation,
            forced.toLong(),
            global.toLong(),
            metaId
        ).ck()
    }

    override fun deleteUIMetaById(metaId: Long) {
        uiMetaQueries.delete(metaId).ck()
    }

    override fun findUIMetasByBid(bid: Long): List<SelectByBid> {
        return uiMetaQueries.selectByBid(bid).executeAsList()
    }

    override fun newMetaValue(
        uId: Long,
        bid: Long,
        metaId: Long,
        metaValue: String
    ): Long {
        val id = getSnowId()
        metaValueQueries.insert(id, uId, metaId, bid, metaValue).ck()
        return id
    }

    override fun updateMetaValueById(metaValueId: Long, metaValue: String) {
        metaValueQueries.update(metaValue, metaValueId).ck()
    }

    override fun findMetasByUidAndBid(
        uid: Long,
        bid: Long
    ): List<Meta> {
        return metaValueQueries.selectMetasByUidAndBid(uid, bid).executeAsList().map {
            Meta(
                it.metaId, it.label ?: "", it.metaType!!, it.metaValueId, it.metaValue
            )
        }
    }
}