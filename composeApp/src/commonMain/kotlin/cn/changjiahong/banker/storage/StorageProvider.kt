package cn.changjiahong.banker.storage

import cn.changjiahong.banker.storage.StorageProvider.homeDir
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.isDirectory
import io.github.vinceglb.filekit.parent
import io.github.vinceglb.filekit.path

expect object StorageProvider {

    val homeDir: String

    fun getTempFilePath(fileName: String): String
}

object Storage {
    val appDir: String = "$homeDir/Documents/Banker"
    val tempDir: String = "$appDir/temp"

    fun getTempFilePath(fileName: String) = StorageProvider.getTempFilePath(fileName)

    val databasesDir: PlatformFile = FileKit.databasesDir.apply {
        if (!exists()) {
            createDirectories()
        }
    }

    val dbFile: PlatformFile = PlatformFile(databasesDir, "Banker.db")

    val templatesDir: PlatformFile = PlatformFile(FileKit.filesDir, "/templates").apply {
        if (!exists()) {
            createDirectories()
        }
    }


    fun getTemplatesFile(fileName: String): PlatformFile {
        return PlatformFile(templatesDir, fileName)
    }
}


fun PlatformFile.containsFile(fileName: String): Boolean {
    if (!this.exists() || !this.isDirectory()) return false
    return PlatformFile(this, fileName).exists()
}

val String.platformFile get() = PlatformFile(this)