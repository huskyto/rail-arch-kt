package com.sillyhusky.arch.rail

interface IRailSystem {

    fun step()
    fun getRegisterValue(reg: Int): Byte
    fun getCntRegisterValue(): Byte
    fun getProgramSlice(start: Int, end: Int): Array<Byte>
    fun getRAMSlice(start: Int, end: Int): Array<Byte>

}