package cn.changjiahong.banker.model

import cafe.adriel.voyager.navigator.tab.Tab
import cn.changjiahong.banker.utils.copyMutable

class Tables(val optionsKey: List<String>, val fieldIds: List<Long>) {

    inner class Row() {
        private val map = mapOf<String, String>()

        fun a() {
            optionsKey
        }
    }

    /**
     * 初始化每张表
     */
    private val tables: Map<Long, List<Row>> = fieldIds.associate { it to emptyList() }

    /**
     * 获取表
     */
    fun table(fieldId: Long): List<Row> {
        val table = tables[fieldId]!!.copyMutable()
        if (table.isEmpty()) {
            table.add(Row())
        }
        return table
    }
}