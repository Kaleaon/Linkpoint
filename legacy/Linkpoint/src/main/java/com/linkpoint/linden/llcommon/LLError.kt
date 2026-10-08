package com.linkpoint.linden.llcommon

import java.util.logging.ConsoleHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

object LLError {

    enum class ELevel { DEBUG, INFO, WARN, ERROR, NONE }

    object Settings {
        @Volatile var minLevel: ELevel = ELevel.DEBUG
        @set:JvmName("setHandlerProp") @Volatile var handler: Handler = ConsoleHandler().also { h ->
            h.formatter = SimpleFormatter()
            h.level = Level.ALL
        }

        fun setLevel(level: ELevel) {
            minLevel = level
        }

        fun setHandler(h: Handler) {
            Log.logger.removeHandler(handler)
            handler = h
            Log.logger.addHandler(h)
        }
    }

    object Log {
        internal val logger: Logger = Logger.getLogger("LLError").also { l ->
            l.useParentHandlers = false
            l.addHandler(Settings.handler)
            l.level = Level.ALL
        }

        fun debug(tag: String, msg: String) {
            if (Settings.minLevel <= ELevel.DEBUG) {
                logger.log(Level.FINE, "[$tag] $msg")
            }
        }

        fun info(tag: String, msg: String) {
            if (Settings.minLevel <= ELevel.INFO) {
                logger.log(Level.INFO, "[$tag] $msg")
            }
        }

        fun warn(tag: String, msg: String) {
            if (Settings.minLevel <= ELevel.WARN) {
                logger.log(Level.WARNING, "[$tag] $msg")
            }
        }

        fun error(tag: String, msg: String) {
            if (Settings.minLevel <= ELevel.ERROR) {
                logger.log(Level.SEVERE, "[$tag] $msg")
            }
        }

        fun isEnabled(level: ELevel): Boolean = level >= Settings.minLevel
    }
}

inline fun llassert(condition: Boolean, msg: String = "") {
    if (!condition) throw AssertionError(if (msg.isEmpty()) "Assertion failed" else msg)
}

inline fun llDebug(tag: String, block: () -> String) {
    if (LLError.Log.isEnabled(LLError.ELevel.DEBUG)) {
        LLError.Log.debug(tag, block())
    }
}

inline fun llInfo(tag: String, block: () -> String) {
    if (LLError.Log.isEnabled(LLError.ELevel.INFO)) {
        LLError.Log.info(tag, block())
    }
}

inline fun llWarn(tag: String, block: () -> String) {
    if (LLError.Log.isEnabled(LLError.ELevel.WARN)) {
        LLError.Log.warn(tag, block())
    }
}

inline fun llError(tag: String, block: () -> String) {
    if (LLError.Log.isEnabled(LLError.ELevel.ERROR)) {
        LLError.Log.error(tag, block())
    }
}
