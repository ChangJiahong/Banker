package cn.changjiahong.banker

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import cn.changjiahong.banker.storage.Storage
import io.github.vinceglb.filekit.path
import java.util.Properties

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFilePath = Storage.dbFile.path
        val driver: SqlDriver =
            JdbcSqliteDriver("jdbc:sqlite:$dbFilePath", Properties(), BankerDb.Schema)
        return driver
    }
}