package com.linkpoint.linden.llcommon

import java.util.Random

object LLRand {
    private val rng = Random()

    fun llRand(val_: Int): Int = rng.nextInt(val_.coerceAtLeast(1))
    fun llFRand(val_: Float): Float = rng.nextFloat() * val_
    fun llDRand(val_: Double): Double = rng.nextDouble() * val_

    fun setSeed(seed: Long) { rng.setSeed(seed) }
}

fun llRand(v: Int): Int = LLRand.llRand(v)
fun llFRand(v: Float): Float = LLRand.llFRand(v)
fun llDRand(v: Double): Double = LLRand.llDRand(v)
