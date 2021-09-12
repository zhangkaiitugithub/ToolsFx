package me.leon

import me.leon.Caesar.virgeneneDecode
import me.leon.Caesar.virgeneneEncode
import org.junit.Test

class ClassicalCrypto {
    @Test
    fun caeser() {
        "ATTACKATDAWN".virgeneneEncode("LEMONLEMONLE").also { println(it) }
        "CRYPTO IS SHORT FOR CRYPTOGRAPHY".virgeneneEncode("ABCDEF AB CDEFA BCD EFABCDEFABCD")
            .also { println(it) }
        "LXFOPVEFRNHR".virgeneneDecode("LEMONLEMONLE").also { println(it) }
    }
}