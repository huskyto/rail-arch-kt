package com.sillyhusky.arch.rail.util

class ByteUtil {

    companion object {
        fun getByteInHex(byte: Byte, prefix: String): String {
            val hexChars = "0123456789ABCDEF"

            val highNibble = (byte.toInt() shr 4) and 0x0F
            val lowNibble = byte.toInt() and 0x0F

            return prefix + hexChars[highNibble] + hexChars[lowNibble]
        }
    }

}