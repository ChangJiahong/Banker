package cn.changjiahong.banker.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

object NoData

typealias FlowList<T> = Flow<List<T>>

typealias OkFlow = Flow<NoData>

fun <T> returnFlow(block: suspend FlowCollector<T>.() -> T): Flow<T> = flow {
    val result = block()
    emit(result)
}

fun okFlow(block: suspend FlowCollector<NoData>.() -> Unit) = flow {
    block()
    emit(NoData)
}