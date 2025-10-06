package cn.changjiahong.banker.model

data class UIMetaField(
    val metaId: Long,
    val label: String,
    val metaType: String,
    val width: Int,
    val options: String,
    val validation: String,
    val forced: Boolean,
    val isGlobal: Boolean,
    val weight: Long,
    val tag: String
) {

}