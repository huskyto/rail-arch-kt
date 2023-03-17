package com.sillyhusky.arch.rail.registers

open class RailRegister {

    private var value: Byte = 0

    open fun get() = value
    open fun set(value: Byte) {
        this.value = value
    }

    override fun toString(): String {
        return "RailRegister: $value"
    }


}