package me.leon

import me.leon.encode.base.BYTE_BITS
import me.leon.ext.toBinaryString

/** https://en.wikipedia.org/wiki/Xxencoding */
const val UU_DICT = "`!\"#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_"

fun String.uuEncode(dict: String = UU_DICT) = toByteArray().uuEncode(dict)

fun ByteArray.uuEncode(dict: String = UU_DICT) =
    toBinaryString().chunked(8 * 45).joinToString("") {
        val leadChar = (it.length / 8 + 32).toChar()
        "$leadChar" +
            it.chunked(BASE64_BLOCK_SIZE).joinToString("") {
                dict.ifEmpty { UU_DICT }[it.padding("0", BASE64_BLOCK_SIZE).toInt(2)].toString()
            } +
            if (leadChar == 'M') System.lineSeparator() else ""
    }

fun String.uuDecode(dict: String = UU_DICT) =
    this.filterNot { it == '\r' || it == '\n' }
        .chunked(61)
        .joinToString("") {
            it.substring(1, (it.first().code - 32) * 4 / 3 + 1)
                .map {
                    dict
                        .ifEmpty { UU_DICT }
                        .indexOf(it)
                        .toString(2)
                        .padding("0", BASE64_BLOCK_SIZE, false)
                }
                .joinToString("")
        }
        .chunked(BYTE_BITS)
        .filter { it.length == BYTE_BITS }
        .map { (it.toInt(2) and BYTE_MASK).toByte() }
        .toByteArray()

fun String.uuDecode2String(dict: String = UU_DICT) = String(uuDecode(dict))
