package cn.changjiahong.banker.utils

fun <T> List<T>.copyMutable(block: MutableList<T>.() -> Unit = {}): MutableList<T> {
    return toMutableList().apply {
        block()
    }
}

fun <T> List<T>.copy(block: MutableList<T>.() -> Unit = {}): List<T> {
    return toMutableList().apply {
        block()
    }
}