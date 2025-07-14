package cn.changjiahong.banker.utils

import app.cash.sqldelight.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


fun <T : Any> Flow<Query<T>>.list(): Flow<List<T>> = map {
    it.executeAsList()
}