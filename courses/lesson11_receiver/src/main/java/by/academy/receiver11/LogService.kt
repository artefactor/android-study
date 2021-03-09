package by.academy.receiver11

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KFunction1
import android.app.NotificationChannel as NotificationChannel1

const val LOG_TAG = "LogService_LOG"
val formatDate = SimpleDateFormat("yyyy/MM/dd")
val formatTime = SimpleDateFormat("HH:mm")

interface UpdateListener {
    fun onUpdate(value: String)
}

class LogService : JobIntentService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    internal var listener: UpdateListener? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(LOG_TAG, "Service onCreate")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "Service onStartCommand ${intent?.action}")
        serviceScope.launch {
            val result = doWork(intent)
            Log.d(LOG_TAG, "Service onUpdate $result")
            listener?.onUpdate(result)
        }

//        showServiceNotification()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(LOG_TAG, "Service onBind")
        return LogBinder(this)
    }


    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(LOG_TAG, "Service onUnbind")
        listener = null
        return super.onUnbind(intent)
    }

    override fun onHandleWork(intent: Intent) {
        serviceScope.launch {
            val result = doWork(intent)
            Log.d(LOG_TAG, "Service onHandleWork $result")
            listener?.onUpdate(result)
        }
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "Service onDestroy")
        super.onDestroy()
        listener = null
    }

    private fun doWork(intent: Intent?): String {
        val action = intent?.action ?: ""
        val time = GregorianCalendar().time
        val entry = LogEntry(formatDate.format(time), formatTime.format(time), action )
        val json: String = Gson().toJson(entry)
        Log.i("file_tag", "writing to file" + Thread.currentThread().name)

        //Все события должны быть записаны в файл в формате JSON.
        File(baseContext.filesDir, "text.txt").appendText("$json\n")

        // можно просто возвращать новою запись и в активити ее просто добавлять в адаптере
        // но мы сделаем по-сложнее и будем считывать там полностью заново
        return json
    }

    // не сработало у меня
    internal fun showServiceNotification() {
        val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel("my_service", "My Background Service")
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentTitle("TestService is working")
                .setContentText("The service has been started in tha background")
                .build()
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel1(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

}

class LogBinder(private val logService: LogService) : Binder() {

    fun setListener(listener: UpdateListener) {
        Log.d(LOG_TAG, "Service LogBinder#setListener$listener  ")
        logService.listener = listener
        logService.stopForeground(true)
    }

    fun clearListener() {
        logService.listener = null
        logService.showServiceNotification()
    }
}
