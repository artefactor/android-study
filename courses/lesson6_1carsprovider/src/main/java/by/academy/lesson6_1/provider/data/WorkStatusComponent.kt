package by.academy.lesson6_1.provider.data

import android.os.Build
import androidx.annotation.RequiresApi
import by.academy.lesson6_1.provider.R


const val WS_IN_PROGRESS = 0
const val WS_COMPLETED = 2
const val WS_PENDING = 4

@RequiresApi(Build.VERSION_CODES.M)
object WorkStatusComponent {

    private const val WS_IN_PROGRESS_COLOR = R.color.yellow
    private const val WS_COMPLETED_COLOR = R.color.green
    private const val WS_PENDING_COLOR = R.color.red

    private const val INACTIVE_COLOR = R.color.purple_700

    fun statusColor(status: Int): Int {
        when (status) {
            WS_IN_PROGRESS -> return WS_IN_PROGRESS_COLOR
            WS_COMPLETED -> return WS_COMPLETED_COLOR
            WS_PENDING -> return WS_PENDING_COLOR
        }
        return WS_IN_PROGRESS_COLOR
    }
}


