package cn.changjiahong.banker

import app.cash.sqldelight.db.SqlDriver

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        TODO("Not yet implemented")
        //        return AndroidSqliteDriver(BankerDb.Schema, context, "Banker.db")
    }
}