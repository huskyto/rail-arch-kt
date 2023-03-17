package com.sillyhusky.arch.rail.instruction

class RailInstructionBlock(val OP: Byte, val ARG1: Byte, val ARG2: Byte, val RESULT: Byte) {

    fun getSubSystem(): RailSubSystem {
        return when ((OP.toInt() and 48) shr 4) {
            0 -> RailSubSystem.ALU
            1 -> RailSubSystem.RAM_STACK
            2 -> RailSubSystem.CU
            3 -> RailSubSystem.Peripheral
            else -> RailSubSystem.None
        }
    }

    fun getInstruction(): RailInstruction {
        val masked = (OP.toInt() and 15)
        return when (getSubSystem()) {
            RailSubSystem.ALU -> getALUInstruction(masked)
            RailSubSystem.CU -> getCUInstruction(masked)
            RailSubSystem.RAM_STACK -> getRAMInstruction(masked)
            RailSubSystem.Peripheral -> getPeripheralInstruction(masked)
            else -> NoInstruction.None
        }
    }

    fun isArg1Immediate(): Boolean {
        return checkBit(OP.toInt(), 128)
    }

    fun isArg2Immediate(): Boolean {
        return checkBit(OP.toInt(), 64)
    }

    fun getRamSource(): Byte {
        return ARG1
    }

    fun getRamAddr(): Byte {
        return ARG2
    }

    fun getRamTarget(): Byte {
        return RESULT
    }

    fun getResult(): Byte {
        return RESULT
    }

    fun getCUAddr(): Byte {
        return RESULT
    }

    private fun checkBit(value: Int, flag: Int): Boolean {
        return (value and flag) == flag
    }

    private fun getALUInstruction(masked: Int): RailALUInstruction {
        return when(masked) {
            0 -> RailALUInstruction.ADD
            1 -> RailALUInstruction.SUB
            2 -> RailALUInstruction.AND
            3 -> RailALUInstruction.OR
            4 -> RailALUInstruction.NOT
            5 -> RailALUInstruction.XOR
            6 -> RailALUInstruction.SHL
            7 -> RailALUInstruction.SHR
            // 8, 9, 10, 11
            12 -> RailALUInstruction.RANSetSeed
            13 -> RailALUInstruction.RANNext
            // 14
            15 -> RailALUInstruction.NOOP
            else -> RailALUInstruction.None
        }
    }
    private fun getCUInstruction(masked: Int): RailCUInstruction {
        return when(masked) {
            0 -> RailCUInstruction.Equals
            1 -> RailCUInstruction.NotEquals
            2 -> RailCUInstruction.LessThan
            3 -> RailCUInstruction.LessEqualThan
            4 -> RailCUInstruction.MoreThan
            5 -> RailCUInstruction.MoreEqualThan
            6 -> RailCUInstruction.TRUE
            7 -> RailCUInstruction.FALSE
            // 8 to 15
            else -> RailCUInstruction.None
        }
    }
    private fun getRAMInstruction(masked: Int): RailRAMInstruction {
        return when(masked) {
            0 -> RailRAMInstruction.Read
            1 -> RailRAMInstruction.Write
            // 2 to 7
            8 -> RailRAMInstruction.SPop
            9 -> RailRAMInstruction.SPush
            10 -> RailRAMInstruction.Ret
            11 -> RailRAMInstruction.Call
            // 12 to 15
            else -> RailRAMInstruction.None
        }
    }
    private fun getPeripheralInstruction(masked: Int): RailPeripheralInstruction {
        return RailPeripheralInstruction.None;
    }

}


