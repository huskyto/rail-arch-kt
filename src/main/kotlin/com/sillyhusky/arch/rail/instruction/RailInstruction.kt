package com.sillyhusky.arch.rail.instruction

interface RailInstruction { }

enum class RailALUInstruction : RailInstruction {
    ADD, SUB, AND, OR, NOT, XOR, SHL, SHR, RANSetSeed, RANNext, NOOP, None
}

enum class RailCUInstruction : RailInstruction {
    Equals, NotEquals, LessThan, LessEqualThan, MoreThan, MoreEqualThan, TRUE, FALSE, None
}
enum class RailRAMInstruction : RailInstruction {
    Read, Write, SPop, SPush, Ret, Call, None
}

enum class RailPeripheralInstruction : RailInstruction {
    None
}

enum class NoInstruction : RailInstruction {
    None
}