package cn.changjiahong.banker.template

import kotlinx.coroutines.flow.Flow

expect object PdfTemplateProcessor {
    suspend  fun fillTemplate(fieldMap: Map<String, String>,fileUri:String): Flow<String>
    suspend  fun fillWordTemplate(fieldMap: Map<String, String>,fileUri:String): Flow<String>
}