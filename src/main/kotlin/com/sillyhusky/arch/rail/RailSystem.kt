package com.sillyhusky.arch.rail

import com.sillyhusky.arch.rail.instruction.*
import com.sillyhusky.arch.rail.registers.RailIORegister
import com.sillyhusky.arch.rail.registers.RailRegister
import java.util.*

class RailSystem {

        // TODO, if necessary make sizes editable. Maybe through Factory.
    private val REGISTERS: Array<RailRegister> = Array(16) { RailRegister() }
    private val RAM: Array<Byte> = Array(256) { 0 }
    private val CALL_STACK: Stack<Byte> = Stack()
    private val PROGRAM: Array<Byte> = Array(256) { 0 }

    constructor() {
        REGISTERS[15] = RailIORegister()
    }

    constructor(program: Array<Byte>) {
        RailSystem()
        for (i in program.indices) {
            this.PROGRAM[i] = program[i]
        }
    }

    fun step() {
        val instruction = getNextInstructionBlock()
        processInstruction(instruction)
    }

    private fun getNextInstructionBlock(): RailInstructionBlock {
        val programCntReg = getCntRegister()
        val cnt = programCntReg.get()
        val ops = PROGRAM.slice(cnt..cnt + 4)
        programCntReg.set((cnt + 4).toByte())

        return RailInstructionBlock(ops[0], ops[1], ops[2], ops[3])
    }

    private fun getCntRegister(): RailRegister {
        return REGISTERS[14]
    }

    private fun processInstruction(instruction: RailInstructionBlock) {
        when (instruction.getSubSystem()) {
            RailSubSystem.ALU -> processALU(instruction)
            RailSubSystem.RAM_STACK -> processRAMStack(instruction)
            RailSubSystem.CU -> processCU(instruction)
            RailSubSystem.Peripheral -> { } // todo
            else -> { }// noop
        }
    }

    private fun processALU(instruction: RailInstructionBlock) {
        val op: RailALUInstruction = instruction.getInstruction() as RailALUInstruction
        val arg1 = getArg1Value(instruction).toInt()
        val arg2 = getArg2Value(instruction).toInt()
        val res: Int = when (op) {
            RailALUInstruction.ADD -> arg1 + arg2
            RailALUInstruction.SUB -> arg1 - arg2
            RailALUInstruction.AND -> arg1 and arg2
            RailALUInstruction.OR  -> arg1 or arg2
            RailALUInstruction.NOT -> arg1.inv()
            RailALUInstruction.XOR -> arg1 xor arg2
            RailALUInstruction.SHL -> arg1 shl arg2
            RailALUInstruction.SHR -> arg1 shr arg2
            RailALUInstruction.RANSetSeed -> TODO()
            RailALUInstruction.RANNext -> TODO()
            RailALUInstruction.NOOP -> -1 // noop
            else -> -1 // noop
        }
        if (res == -1) return

        val resReg = REGISTERS[instruction.getResult().toInt()]
        resReg.set(res.toByte())
    }

    private fun processRAMStack(instruction: RailInstructionBlock) {
        val op = instruction.getInstruction() as RailRAMInstruction
        val source = getArg1Value(instruction).toInt()
        val addr = getArg2Value(instruction).toInt()
        val target = instruction.getRamTarget()
        val targetReg = REGISTERS[target.toInt()]
        when (op) {
            RailRAMInstruction.Read -> targetReg.set(RAM[addr])
            RailRAMInstruction.Write -> RAM[addr] = REGISTERS[source].get()
            RailRAMInstruction.SPop -> TODO()
            RailRAMInstruction.SPush -> TODO()
            RailRAMInstruction.Ret -> {
                getCntRegister().set(CALL_STACK.pop())
            }
            RailRAMInstruction.Call -> {
                CALL_STACK.push((getCntRegister().get()))  //already moved to next in step
                getCntRegister().set(source.toByte())
            }
            RailRAMInstruction.None -> { } //noop
        }
    }

    private fun processCU(instruction: RailInstructionBlock) {
        val op = instruction.getInstruction() as RailCUInstruction
        val arg1 = getArg1Value(instruction).toInt()
        val arg2 = getArg2Value(instruction).toInt()
        val jmpAddr = instruction.getCUAddr()   // always immediate
        val doJmp: Boolean = when (op) {
            RailCUInstruction.Equals -> arg1 == arg2
            RailCUInstruction.NotEquals -> arg1 != arg2
            RailCUInstruction.LessThan -> arg1 < arg2
            RailCUInstruction.LessEqualThan -> arg1 <= arg2
            RailCUInstruction.MoreThan -> arg1 > arg2
            RailCUInstruction.MoreEqualThan -> arg1 >= arg2
            RailCUInstruction.TRUE -> true
            RailCUInstruction.FALSE -> false
            else -> false
        }
        if (doJmp) {
            getCntRegister().set(jmpAddr)
        }
    }

    private fun getArg1Value(instruction: RailInstructionBlock): Byte {
        return if (instruction.isArg1Immediate()) instruction.ARG1
        else REGISTERS[instruction.ARG1.toInt()].get()
    }
    private fun getArg2Value(instruction: RailInstructionBlock): Byte {
        return if (instruction.isArg2Immediate()) instruction.ARG2
        else REGISTERS[instruction.ARG2.toInt()].get()
    }

}