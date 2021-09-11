package me.leon

object QuotePrintable {

    fun encode(src: String) =
        src.toCharArray()
            .map {
                when {
                    it.isDigit() || it.isLowerCase() || it.isUpperCase() -> it
                    it.code < 16 -> "%0${it.code.toString(16)}"
                    it.code < 256 -> "%${it.code.toString(16)}"
                    else -> "%u${it.code.toString(16)}"
                }
            }
            .joinToString("")

    fun decode(src: String): String {

        val tmp = StringBuilder(src.length)
        var lastPos = 0
        var pos: Int
        var ch: Char

        while (lastPos < src.length) {
            pos = src.indexOf("%", lastPos)
            if (pos == lastPos) {
                if (src[pos + 1] == 'u') {
                    ch = src.substring(pos + 2, pos + 6).toInt(16).toChar()
                    tmp.append(ch)
                    lastPos = pos + 6
                } else {
                    ch = src.substring(pos + 1, pos + 3).toInt(16).toChar()
                    tmp.append(ch)
                    lastPos = pos + 3
                }
            } else {
                lastPos =
                    if (pos == -1) {
                        tmp.append(src.substring(lastPos))
                        src.length
                    } else {
                        tmp.append(src.substring(lastPos, pos))
                        pos
                    }
            }
        }
        return tmp.toString()
    }
}

fun String.quotePrintable() = QuotePrintable.encode(this)

fun ByteArray.quotePrintable() = String(this).quotePrintable()

fun String.quotePrintableDecode() = quotePrintableDecode2String().toByteArray()

fun String.quotePrintableDecode2String() = QuotePrintable.decode(this)
