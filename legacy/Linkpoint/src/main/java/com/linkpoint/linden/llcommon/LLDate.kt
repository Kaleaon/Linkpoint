package com.linkpoint.linden.llcommon

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class LLDate(val secondsSinceEpoch: Double = 0.0) : Comparable<LLDate> {

    fun isNull(): Boolean = secondsSinceEpoch == 0.0

    fun notNull(): Boolean = secondsSinceEpoch != 0.0

    fun toISOString(): String {
        val instant = Instant.ofEpochMilli((secondsSinceEpoch * 1000.0).toLong())
        val subsecMillis = ((secondsSinceEpoch * 1000.0).toLong() % 1000).toInt()
            .let { if (it < 0) it + 1000 else it }
        val base = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            .withZone(ZoneOffset.UTC)
            .format(instant)
        return if (subsecMillis > 0) {
            val centiseconds = subsecMillis / 10
            "$base.${centiseconds.toString().padStart(2, '0')}Z"
        } else {
            "${base}Z"
        }
    }

    operator fun plus(other: LLDate): LLDate = LLDate(secondsSinceEpoch + other.secondsSinceEpoch)

    operator fun minus(other: LLDate): LLDate = LLDate(secondsSinceEpoch - other.secondsSinceEpoch)

    operator fun plus(seconds: Double): LLDate = LLDate(secondsSinceEpoch + seconds)

    operator fun minus(seconds: Double): LLDate = LLDate(secondsSinceEpoch - seconds)

    override fun compareTo(other: LLDate): Int = secondsSinceEpoch.compareTo(other.secondsSinceEpoch)

    companion object {
        val NULL: LLDate = LLDate(0.0)

        fun now(): LLDate {
            val epochMillis = System.currentTimeMillis()
            return LLDate(epochMillis / 1000.0)
        }

        fun fromISOString(s: String): LLDate? {
            val normalized = when {
                s.endsWith("Z") -> s
                s.length == 19 -> "${s}Z"
                else -> s
            }
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SS][.S]X")
                    .withZone(ZoneOffset.UTC)
                val instant = Instant.from(formatter.parse(normalized))
                LLDate(instant.toEpochMilli() / 1000.0)
            } catch (_: DateTimeParseException) {
                null
            }
        }

        fun fromSeconds(seconds: Double): LLDate = LLDate(seconds)
    }
}
