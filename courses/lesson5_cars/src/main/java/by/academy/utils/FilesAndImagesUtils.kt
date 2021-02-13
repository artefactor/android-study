package by.academy.utils

import android.content.Context
import android.util.Log
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

object FilesAndImagesUtils {
    /**
     * •	При каждом запуске приложения делать запись в файле,
     * который должен выступать в качестве журнала событий (лог).
     * Туда надо записывать дату и время запуска приложения.
     * •	Файл должен храниться в закрытом хранилище.
     */
    @JvmStatic
    fun appendLogFile(context: Context, filename: String) {
        val text = """started on ${Date()}
             """.trimIndent()
        Log.i(LoggingTags.TAG_FILES, "onCreate: writing text to file:$text")
        val file = File(context.filesDir, filename)
        try {
            val writer = FileWriter(file, true)
            val bufferWriter = BufferedWriter(writer)
            bufferWriter.write(text)
            bufferWriter.close()
        } catch (e: IOException) {
            println(e)
            Log.e(LoggingTags.TAG_FILES, "error during work with file", e)
        }
    }

    @Throws(IOException::class)
    fun createImageFile(fileDir: File): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = File(fileDir, "images")
        if (storageDir.exists() && !storageDir.isDirectory) {
            val delete = storageDir.delete()
        }
        if (!storageDir.exists()) {
            val mkdir = storageDir.mkdir()
        }
        Log.i(LoggingTags.TAG_FILES, "createImageFile: $storageDir")
        Log.i(LoggingTags.TAG_FILES, "createImageFile ${storageDir.exists()} ${storageDir.canWrite()} ")
        val file = File(storageDir, imageFileName)
        if (!file.exists()) {
            val newFile = file.createNewFile()
        }
        return file
    }

}