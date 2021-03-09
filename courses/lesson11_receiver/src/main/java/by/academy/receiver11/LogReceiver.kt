package by.academy.receiver11

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat


class LogReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val intentAction = intent?.action ?: "no name"
        Log.d(LOG_TAG, "Receiver $intentAction")
        ContextCompat.startForegroundService(context, Intent(context, LogService::class.java).apply { action = intentAction })
    }
}