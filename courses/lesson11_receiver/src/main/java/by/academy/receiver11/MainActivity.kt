package by.academy.receiver11

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


class MainActivity : AppCompatActivity(), UpdateListener {
    private var bound: Boolean = false
    private var serviceConnection: ServiceConnection? = null
    private lateinit var logBinder: LogBinder
    private val logItemsAdapter = LogItemsAdapter()

    override fun onUpdate(value: String) = updateLogs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            adapter = logItemsAdapter.apply { items = arrayListOf() }
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }

        setupService()
        Log.d(LOG_TAG, "MainActivity onCreated")
    }

    private fun setupService() {
        serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                Log.d(LOG_TAG, "MainActivity onServiceConnected")
                logBinder = binder as LogBinder
                logBinder.setListener(this@MainActivity)
                bound = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Log.d(LOG_TAG, "MainActivity onServiceDisconnected")
                bound = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sortMenu -> if (logItemsAdapter.changeSort()) {
                item.icon = baseContext.getDrawable(R.drawable.ic_baseline_sort_desc_24)
            } else {
                item.icon = baseContext.getDrawable(R.drawable.ic_baseline_sort_24)
            }
            R.id.clean -> cleanLogs()
            R.id.manualUpdate -> updateLogs()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, LogService::class.java)
        serviceConnection?.let { bindService(intent, it, Context.BIND_AUTO_CREATE) }
        Log.d(LOG_TAG, "MainActivity onStart")
        updateLogs()
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "MainActivity onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        logBinder.clearListener()
        // зануляем
        bound = false
        serviceConnection = null
        Log.d(LOG_TAG, "MainActivity onDestroy")
    }

    private fun updateLogs() {
        GlobalScope.launch(Dispatchers.IO) {
            val deferred = async {
                val file = File(baseContext.filesDir, "text.txt")
                if (file.exists() && file.canRead()) {
                    Log.i("file_tag", "reading file")
                    val list = file.useLines {
                        it.map { line -> Gson().fromJson(line, LogEntry::class.java) }.toList()
                    }
                    launch(Dispatchers.Main) {
                        Log.d(LOG_TAG, "updateLogs ${Thread.currentThread().name}")
                        logItemsAdapter.items = list
                    }
                }
            }
        }
    }

    private fun cleanLogs() =
            GlobalScope.launch(Dispatchers.IO) {
                val deferred = async {
                    val file = File(baseContext.filesDir, "text.txt")
                    if (file.exists() && file.canRead()) {
                        file.writeText("")
                        launch(Dispatchers.Main) {
                            logItemsAdapter.items = emptyList()
                        }
                    }
                }
            }
}