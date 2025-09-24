package com.money

class Logger private constructor() {
    companion object {
        fun debug(message: String = "") = log(message, LogLevel.DEBUG)
        fun info(message: String = "") = log(message, LogLevel.INFO)
        fun warn(message: String = "") = log(message, LogLevel.WARN)
        fun error(message: String = "") = log(message, LogLevel.ERROR)

        private fun log(message: String = "", level: LogLevel) {
            println(message + " [${level.name}]")
        }
    }
}

enum class LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
}