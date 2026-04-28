package com.linkpoint.linden.llmath

// Return difference between lhs and rhs treating operands as unsigned values of the given bit width.
// Modular arithmetic ensures wrap-around is masked to width bits.
fun modularSubtract(lhs: UInt, rhs: UInt, width: Int): UInt {
    val mask = (1u shl width) - 1u
    return mask and (lhs - rhs)
}
