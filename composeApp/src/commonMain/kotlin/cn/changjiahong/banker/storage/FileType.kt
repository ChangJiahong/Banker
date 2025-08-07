package cn.changjiahong.banker.storage

object FileType {

    const val DIR = "DIR"
    const val PDF = "PDF"
    const val DOC = "DOC"
    const val DOCX = "DOCX"
    const val XLS = "XLS"
    const val XLSX = "XLSX"

    val SupportedType = listOf(PDF, DOC, DOCX, XLS, XLSX)

    fun supported(fileType: String): Boolean {
        return fileType.uppercase() in SupportedType
    }

    fun getFileType(fileType: String): String {
        return fileType.uppercase()
    }
}