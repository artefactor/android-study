@file:JvmName("PermissionsHelper")
package by.academy.lesson8.part2

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KFunction0

@RequiresApi(api = Build.VERSION_CODES.N)
fun checkPermission(context: Activity, permission: Array<String>, reqCode: Int): Boolean {
    val currentAPIVersion = Build.VERSION.SDK_INT
    if (currentAPIVersion < Build.VERSION_CODES.M) {
        return true
    }
    val necessaryPerms = Arrays.stream(permission)
            .filter { p: String -> ContextCompat.checkSelfPermission(context, p) != PackageManager.PERMISSION_GRANTED }
            .collect(Collectors.toList())
    if (necessaryPerms.isEmpty()) {
        return true
    }
    ActivityCompat.requestPermissions(context, necessaryPerms.toTypedArray(), reqCode)
    return false
}

fun notGivenPermission3(grantResults: IntArray, permissions: Array<out String>, funSuccess: KFunction0<Unit>, funError: (String) -> Unit) {
    val notGivenPermission = StringBuilder()
    for (i in grantResults.indices) {
        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
            notGivenPermission.append(" ").append(permissions[i])
        }
    }
    if (notGivenPermission.isEmpty()) {
        funSuccess()
    } else {
        funError.invoke(notGivenPermission.toString())
    }
}

