package com.sillyhusky.arch.rail.assembler

interface RASMLine {
    fun getComment(): String
}

open class RASMBaseLine(private val comment: String) : RASMLine {

    override fun getComment(): String {
        return comment
    }

}

class RASMTagLine(val tagValue: RASMTag, val tags: Array<String>, comment: String) : RASMBaseLine(comment) {
    constructor(tagValue: RASMTag, tags: Array<String>) : this(tagValue, tags, "")

}

class RASMCodeLine(val parts: Array<String>, comment: String) : RASMBaseLine(comment) {
    constructor(parts: Array<String>) : this(parts, "")
}

class RASMEmptyLine(comment: String) : RASMBaseLine(comment) {
    constructor() : this("")
}

enum class RASMTag {
    CONST, LABEL
}
