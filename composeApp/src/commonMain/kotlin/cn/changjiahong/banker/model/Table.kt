package cn.changjiahong.banker.model

import cn.changjiahong.banker.utils.copyMutable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.iterator

fun String.isTableType(): Boolean {
    return this in arrayOf("ROW_TABLE")
}

class Table(
    val optionsKey: List<String> = emptyList(),
    val fieldIds: Long,
    val fieldValueId: Long = -1,
    fieldValue: String = ""
) {

    /**
     * 初始化每张表
     */
    private val table: MutableList<Row> = mutableListOf()

    init {
        if (fieldValue.isNotBlank()) {
            try {
                val ll = Json.decodeFromString<List<Map<String, String>>>(fieldValue)
                this.table.addAll(ll.mapIndexed { ind, map -> Row(ind, map) })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private constructor(table: Table) : this(
        table.optionsKey,
        table.fieldIds,
        table.fieldValueId,
        table.toFieldValue()
    ) {
    }


    fun mapToJson(map: Map<String, Any?>): String {
        val jsonObj = JsonObject(map.mapValues { JsonPrimitive(it.value.toString()) })
        return Json.encodeToString(jsonObj)
    }

    fun jsonToMap(json: String): Map<String, String> {
        val obj = Json.parseToJsonElement(json).jsonObject
        return obj.mapValues { it.value.jsonPrimitive.content }
    }


    inner class Row(val index: Int) {
        private val map = optionsKey.associateWith { "" }.toMutableMap()

        private constructor(row: Row) : this(row.index) {
            map.putAll(row.map)
        }

        constructor(index: Int, map: Map<String, String>) : this(index) {
            this.map.putAll(map)
        }

        fun set(key: String, value: String) {
            map[key] = value
        }

        fun copy(key: String, value: String): Row {
            return Row(this).apply {
                map[key] = value
            }
        }

        operator fun iterator(): Iterator<Map.Entry<String, String>> {
            return map.toMap().iterator()
        }

        override fun toString(): String {
            return map.toString()
        }

        fun map(): Map<String, String> {
            return map
        }

    }

    /**
     * 获取表
     */
    fun table(): Table {
        if (table.isEmpty()) {
            createRow()
        }
        return this
    }

    fun createRow() {
        table.add(Row(table.size))
    }

    fun newRow(): Row {
        return Row(table.size)
    }

    fun updateRow(row: Row) {
        table[row.index] = row
    }

    fun copy(block: Table.() -> Unit = {}): Table {
        return Table(this).apply {
            block()
        }
    }

    fun toFieldValue(): String {
        val mm = table.map { tableRow -> tableRow.map() }
        return Json.encodeToString(mm)
    }

    operator fun iterator(): Iterator<Row> {
        return table.iterator()
    }
}