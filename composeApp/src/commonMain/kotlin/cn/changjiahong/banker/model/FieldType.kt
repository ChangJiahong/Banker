package cn.changjiahong.banker.model

const val TEXT = "TEXT"
const val IMAGE = "IMAGE"
const val ROW_TABLE = "ROW_TABLE"
const val COL_TABLE = "COL_TABLE"
const val TABLE = "TABLE"

fun fieldTypes() = listOf(TEXT, IMAGE, ROW_TABLE, COL_TABLE, TABLE)


fun String.isTableType(): Boolean {
    return this in listOf(ROW_TABLE, COL_TABLE, TABLE)
}

fun String.isRowTableType(): Boolean{
    return this == ROW_TABLE
}

fun String.isColTableType(): Boolean{
    return this == COL_TABLE
}


fun String.isTextType(): Boolean {
    return this == TEXT
}