package by.academy.lesson6_1.provider

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.academy.lesson6_1.provider.data.WorkDataStorage

class MainActivity : AppCompatActivity() {
    private lateinit var noWorksView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.work_list)

        // Recycler view and adapter
        noWorksView = findViewById<View>(R.id.no_cars).apply { visibility = View.INVISIBLE }

        val workAdapter = WorkDataItemAdapter(
                WorkDataStorage(this@MainActivity.contentResolver),
                resources,
                this@MainActivity::onCheckVisibility
        ).apply {
            addFilteringBy(findViewById(R.id.searchView))
        }

        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = workAdapter
        }
    }

    private fun onCheckVisibility(invisible: Boolean) {
        if (invisible) {
            noWorksView.visibility = View.INVISIBLE
        } else {
            noWorksView.visibility = View.VISIBLE
        }
    }
}