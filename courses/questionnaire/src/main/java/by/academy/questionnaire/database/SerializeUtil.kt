package by.academy.questionnaire.database

import java.io.ByteArrayInputStream
import java.io.Serializable
import java.io.ByteArrayOutputStream
import java.io.ObjectInput
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

@Suppress("UNCHECKED_CAST")
fun <T : Serializable> fromByteArray(byteArray: ByteArray): T {
    val byteArrayInputStream = ByteArrayInputStream(byteArray)
    val objectInput: ObjectInput = ObjectInputStream(byteArrayInputStream)
    val result = objectInput.readObject() as T
    objectInput.close()
    byteArrayInputStream.close()
    return result
}

fun Serializable.toByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
    objectOutputStream.writeObject(this)
    objectOutputStream.flush()
    val result = byteArrayOutputStream.toByteArray()
    byteArrayOutputStream.close()
    objectOutputStream.close()
    return result
}