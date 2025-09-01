package cn.changjiahong.banker

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import cn.changjiahong.banker.model.BError
import org.koin.core.annotation.Single

@Single
expect class DriverFactory() {
    fun createDriver(): SqlDriver
}

@Single
fun createDatabase(driverFactory: DriverFactory): BankerDb {
    val driver = driverFactory.createDriver()
    val database = BankerDb(driver)
    return database
}


fun QueryResult<Long>.ck() {
    if (value <= 0) {
        throw BError.SqlExecuteError("sql 执行失败")
    }
}


class ExecuteError(msg: String = "") : RuntimeException(msg)