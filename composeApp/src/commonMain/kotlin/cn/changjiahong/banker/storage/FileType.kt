package cn.changjiahong.banker.storage

object FileType {

    const val DIR = "DIR"
    const val PDF = "PDF"
    const val DOCX = "DOCX"
    const val XLSX = "XLSX"

    val SupportedType = listOf(PDF, DOCX, XLSX)

    fun supported(fileType: String): Boolean {
        return fileType.uppercase() in SupportedType
    }

    fun getFileType(fileType: String): String {
        return fileType.uppercase()
    }
}