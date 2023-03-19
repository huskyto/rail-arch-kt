package com.sillyhusky.arch.rail.ui

import com.sillyhusky.arch.rail.IRailSystem
import com.sillyhusky.arch.rail.util.ByteUtil

private const val A_GREEN = "\u001B[32m"
private const val A_RESET = "\u001B[0m"
private const val A_BLUE = "\u001B[34m"
private const val A_YELLOW = "\u001B[33m"

class RailConsoleGUI {

    private val height = 32
    private val regSectionWidth = 20
    private val programSectionWidth = 50
    private val ramSectionWidth = 50
    private val width = regSectionWidth + programSectionWidth + ramSectionWidth
    private val border = "#"
    private val sectionBorder = "|"


    private lateinit var railSystem: IRailSystem

    fun init (system: IRailSystem) {
        this.railSystem = system
    }

    fun draw() {
        clear()
        println(getHeader())
        for (i in 0 until  height + 1) println(getLine(i))
    }

    private fun clear() {
        for (i in 0 until height * 2) println()
    }

    private fun getHeader(): String {
        val sb = StringBuilder()
        sb.append(border.repeat(width))
        sb.append("\n$border")
        val regTitle = "$A_YELLOW Registers $A_RESET"
        sb.append(center(regTitle,regSectionWidth + 7))
        sb.append(border)
        val programTitle = "$A_YELLOW Program $A_RESET"
        sb.append(center(programTitle,programSectionWidth + 1))
        sb.append(border)
        val ramTitle  = "$A_YELLOW RAM $A_RESET"
        sb.append(center(ramTitle, ramSectionWidth + 15))
        sb.append(border)

        return sb.toString()
    }

    private fun center(string: String, size: Int): String {
        val extra = (size - string.length) / 2
        return string.padStart(string.length + extra, ' ')
            .padEnd(size, ' ')
    }

    private fun getLine(lineNum: Int): String {
        return if (lineNum == 0 || lineNum == height) {
            border.repeat(width)
        } else {
            getLeft(lineNum) + getCenter(lineNum-1) + getRight(lineNum-1)
        }
    }

    private fun getLeft(lineNum: Int): String {
        val value = when (lineNum - 1) {
            1 -> "R0:  $A_BLUE${toHex(railSystem.getRegisterValue(0))}$A_RESET"
            2 -> "R1:  $A_BLUE${toHex(railSystem.getRegisterValue(1))}$A_RESET"
            3 -> "R2:  $A_BLUE${toHex(railSystem.getRegisterValue(2))}$A_RESET"
            4 -> "R3:  $A_BLUE${toHex(railSystem.getRegisterValue(3))}$A_RESET"
            5 -> "R4:  $A_BLUE${toHex(railSystem.getRegisterValue(4))}$A_RESET"
            6 -> "R5:  $A_BLUE${toHex(railSystem.getRegisterValue(5))}$A_RESET"
            7 -> "R6:  $A_BLUE${toHex(railSystem.getRegisterValue(6))}$A_RESET"
            8 -> "R7:  $A_BLUE${toHex(railSystem.getRegisterValue(7))}$A_RESET"

            9  -> "BZ0: $A_BLUE${toHex(railSystem.getRegisterValue(8))}$A_RESET"
            10 -> "LV0: $A_BLUE${toHex(railSystem.getRegisterValue(9))}$A_RESET"
            11 -> "D0:  $A_BLUE${toHex(railSystem.getRegisterValue(10))}$A_RESET"
            12 -> "D1:  $A_BLUE${toHex(railSystem.getRegisterValue(11))}$A_RESET"
            13 -> "D2:  $A_BLUE${toHex(railSystem.getRegisterValue(12))}$A_RESET"
            14 -> "D3:  $A_BLUE${toHex(railSystem.getRegisterValue(13))}$A_RESET"
            15 -> "CNT: $A_GREEN${toHex(railSystem.getRegisterValue(14))}$A_RESET"
            16 -> "IO:  $A_YELLOW${toHex(railSystem.getRegisterValue(15))}$A_RESET"

            else -> "$A_BLUE$A_RESET"  // makes aligning stuff easier
        }

        return "$border   " + value.padEnd(regSectionWidth + 4, ' ') + sectionBorder
    }

//    private fun getCenter(lineNum: Int): String {
//        val cnt = railSystem.getCntRegisterValue()
//        val slice = railSystem.getProgramSlice(cnt.toInt() + lineNum * 4, cnt.toInt() + (lineNum * 4) + 3)
//        val sb = StringBuilder()
//        sb.append(" ")
//        slice.forEach { byte ->
//            sb.append(toHex(byte)).append(" ")
//        }
//
//        return sb.toString().padEnd(programSectionWidth - 2, ' ') + sectionBorder
//    }

    private fun getCenter(lineNum: Int): String {
        val cnt = railSystem.getCntRegisterValue()
        val sliceLeft = railSystem.getProgramSlice(lineNum * 4, (lineNum * 4) + 3)
        val sliceRight = railSystem.getProgramSlice((lineNum * 4) + 128, (lineNum * 4) + 3 + 128)
        val sb = StringBuilder()
        sb.append(" ".repeat(4))
        var isExecuting = cnt.toInt() / 4 == lineNum
        addProgramSlice(isExecuting, sb, sliceLeft)
        sb.append("   ")

        isExecuting = cnt.toInt() / 4 == lineNum + 32
        addProgramSlice(isExecuting, sb, sliceRight)

        return sb.toString().padEnd(programSectionWidth + 10, ' ') + sectionBorder
    }

    private fun addProgramSlice(isExecuting: Boolean, sb: StringBuilder, slice: Array<Byte>) {
        if (isExecuting) sb.append(" $A_GREEN> ")
        else sb.append(" $A_GREEN$A_RESET  ")

        slice.forEach { sb.append(toHex(it)).append(" ") }
        if (isExecuting) sb.append(A_RESET)
    }

    private fun getRight(lineNum: Int): String {
        val sliceLeft = railSystem.getRAMSlice(lineNum * 4, (lineNum * 4) + 3)
        val sliceRight = railSystem.getRAMSlice((lineNum * 4) + 128, (lineNum * 4) + 3 + 128)
        val sb = StringBuilder()
        sb.append(" ".repeat(4))
        sb.append("  ")
        sliceLeft.forEach { sb.append(toHex(it)).append(" ") }
        sb.append("   ")
        sliceRight.forEach { sb.append(toHex(it)).append(" ") }

        return sb.toString().padEnd(ramSectionWidth + 6, ' ') + border
    }

    private fun toHex(byte: Byte): String {
        return ByteUtil.getByteInHex(byte, "")
    }

}