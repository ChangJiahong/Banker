package cn.changjiahong.banker.model

sealed class BError(message: String):RuntimeException(message){
    class SqlExecuteError(msg: String): BError(msg)
    class Fail(msg: String): BError(msg)
}