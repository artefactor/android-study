package by.academy.questionnaire.database

import junit.framework.TestCase

class SerializeUtilKtTest : TestCase() {
    fun testFromByteArray() {
        val s = "hi"
        val toByteArray: ByteArray = s.toByteArray()
        val s1: String = fromByteArray(toByteArray)
        assertEquals(s,s1)
    }
    fun testFromByteArray2() {
        val s: Array<Int> = arrayOf(2,3,4)
        val toByteArray: ByteArray = s.toByteArray()
        val s1: Array<Int> = fromByteArray(toByteArray)
        assertEquals(s.joinToString(" "),s1.joinToString(" "))
        assertEquals(s.joinToString(","), "2,3,4")
    }

}