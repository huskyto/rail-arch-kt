package com.sillyhusky.arch.rail.assembler

// keywords
private const val LABEL = "LABEL"
private const val CONST = "CONST"

class RailAssembler {


    fun assemble(code: String): Array<Byte> {
        val lines = parseLines(code)
        val intLines = processLines(lines)
        return intLines.map { (it and 0xFF).toByte() }.toTypedArray()
    }
    private fun parseLines(lines: String): Array<RASMLine> {
        val lines = lines.split("\n")
        val result = mutableListOf<RASMLine>()

        lines.forEach { line ->
                // code is returned in uppercase
            val (code, comment) = extractComment(line)

            if (code.isEmpty()) {
                result.add(RASMEmptyLine(comment))
            }
            else {
                    // check for tags
                if (code.startsWith(LABEL)) {
                    val parts = code.split(" ").map { it.trim() }.filter { it.isNotEmpty() }
                    if (parts.size < 2) throw AssemblerException("Label has no value")
                    result.add(RASMTagLine(RASMTag.LABEL, parts.slice(1..1).toTypedArray(), comment))
                }
                else if (code.startsWith(CONST)) {
                    val parts = code.split(" ").map { it.trim() }.filter { it.isNotEmpty() }
                    if (parts.size < 3) throw AssemblerException("Incomplete Const")
                    result.add(RASMTagLine(RASMTag.CONST, parts.slice(1..2).toTypedArray(), comment))
                }
                else {
                    val parts = code.split(" ").map { it.trim() }.filter { it.isNotEmpty() }
                    result.add(RASMCodeLine(parts.toTypedArray(), comment))
                }
            }
        }

        return result.toTypedArray()
    }

    private fun extractComment(line: String): Pair<String, String> {
        var comment = ""
        var code = ""
        if (line.contains("#")) {   // has a comment
            val parts = line.split("#")
            if (parts.isNotEmpty()) code = parts[0].trim()
            if (parts.size > 1) comment = parts[1]
        } else {
            code = line.trim()
        }

        return Pair(code.uppercase(), comment)
    }

    private fun processLines(lines: Array<RASMLine>): Array<Int> {
        val constMap = mutableMapOf<String, String>()
        val labelMap = mutableMapOf<String, Int>()
        val result = mutableListOf<Int>()
        val codeLines = mutableListOf<RASMCodeLine>()
        var codeLine = 0
        lines.forEach { line ->
            when (line) {
                is RASMEmptyLine -> { } //noop
                is RASMTagLine -> {
                    if (line.tagValue == RASMTag.CONST) {
                        constMap[line.tags[0]] = line.tags[1]
                    }
                    else if (line.tagValue == RASMTag.LABEL) {
                        labelMap[line.tags[0]] = codeLine * 4
                    }
                }
                is RASMCodeLine -> {
                            // process later, so labels are correct
                    codeLines.add(line)
                    codeLine++
                }
            }
        }
        codeLines.forEach { line ->
            assert(line.parts.size == 4)
            line.parts.forEach { code ->
                result.add(processCode(code, constMap, labelMap))
            }
        }

        return result.toTypedArray()
    }

    private fun processCode(code: String, constMap: Map<String, String>, labelMap: Map<String, Int>): Int {
        val parts = code.split("+") // add more arithmetic support
        var result = 0
        for (i in parts.indices) {
            var realCode: String = parts[i]
            var numCode = 0
            while (constMap.contains(realCode)) {
                realCode = constMap[realCode]!!
            }
            if (labelMap.contains(realCode)) {
                numCode = labelMap[realCode]!!
            }
            else {
                numCode = RASMDictionary.translate(realCode)
                if (numCode == -1) {
                    numCode = Integer.decode(realCode)
                }
            }

            result += numCode
        }

        return result
    }

    private fun replaceAll(parts: Array<String>, map: Map<String, String>): Array<String> {
        var newArray = Array(parts.size) { "" }
        for (i in parts.indices) {
            var currentString = parts[i]
            map.forEach { entry ->
                currentString = currentString.replace(entry.key, entry.value)
            }
            newArray[i] = currentString
        }

        return newArray
    }

}
