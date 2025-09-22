package cn.changjiahong.banker.utils

/**
 *
 * @author ChangJiahong
 * @date 2025/9/9
 */

fun Long.toBoolean(): Boolean {
    return this != 0L
}

fun Long.toId(): Long? {
    return if (this < 0) null else this
}

fun Boolean.toLong(): Long {
    return if (this) 1 else 0
}