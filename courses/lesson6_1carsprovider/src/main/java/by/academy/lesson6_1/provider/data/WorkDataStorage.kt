package by.academy.lesson6_1.provider.data

import android.content.ContentResolver
import android.net.Uri
import java.util.*

class WorkDataStorage(private val contentResolver: ContentResolver) {

    private val AUTHORITY = "by.academy.lesson5.cars.workInfoProvider"
    private val WORK_PATH = "work_info"

    private val URI_PATH = Uri.parse("content://$AUTHORITY/$WORK_PATH")

    fun getAllWorks(): ArrayList<WorkInfoEntity> {
        val workList = arrayListOf<WorkInfoEntity>()
        val cursor = contentResolver.query(URI_PATH, null, null, null, null)
        cursor?.run {
            while (moveToNext()) {
                workList.add(WorkInfoEntity(
                        getLong(getColumnIndex("id")),
                        Date(getString(getColumnIndex("date")).toLong()),
                        getString(getColumnIndex("title")),
                        getInt(getColumnIndex("status")),
                        getDouble(getColumnIndex("cost")),
                        getString(getColumnIndex("description")),
                        getLong(getColumnIndex("car_id")),
                ))
            }
            cursor.close()
        }
        return workList
    }
}