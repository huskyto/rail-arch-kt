package com.sillyhusky.arch.rail.registers

class RailIORegister : RailRegister() {

    override fun set(value: Byte) {
        super.set(value)
        println(value)
    }

    override fun get(): Byte {
        return 0
            // TODO
    }
}