package by.academy.lesson5.cars.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import by.academy.lesson5.cars.BuildConfig
import by.academy.lesson5.cars.data.DatabaseInfo
import by.academy.lesson5.cars.data.DatabaseInfo.Companion.init

class WorkProvider : ContentProvider() {
    private var database: DatabaseInfo? = null

    companion object {
        private const val AUTHORITY = BuildConfig.APPLICATION_ID + ".workInfoProvider"
        private const val URI_USER_CODE = 21
        private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "work_info", URI_USER_CODE)
        }
    }

    override fun onCreate(): Boolean {
        database = context?.let { init(it.applicationContext).value }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun query(p0: Uri, p1: Array<out String>?, p2: String?, p3: Array<out String>?, p4: String?) =
            if (uriMatcher.match(p0) == URI_USER_CODE) {
                database?.getWorkInfoDAO()?.selectAll()?.apply {
//                    setNotificationUri(requireContext().contentResolver,
//                            Uri.parse("content://" + AUTHORITY + "/" + "work_info")
//                    )
                }
            } else null

    override fun getType(p0: Uri) = "object/*"

    override fun insert(p0: Uri, p1: ContentValues?) = null

    override fun delete(p0: Uri, p1: String?, p2: Array<out String>?) = 0

    override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<out String>?) = 0
}