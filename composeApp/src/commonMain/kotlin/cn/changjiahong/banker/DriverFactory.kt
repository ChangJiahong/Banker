package cn.changjiahong.banker

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
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
        throw ExecuteError()
    }
}


class ExecuteError(msg: String="") : RuntimeException(msg)