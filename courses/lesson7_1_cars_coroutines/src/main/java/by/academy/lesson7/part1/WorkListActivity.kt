package by.academy.lesson7.part1

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.academy.lesson7.part1.data.AbstractDataRepository
import by.academy.lesson7.part1.data.CarInfoEntity
import by.academy.lesson7.part1.data.RepositoryFactory
import by.academy.lesson7.part1.data.WorkInfoEntity
import by.academy.utils.TextWatcherAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

const val REQUEST_CODE_WORKS = 28

class WorkListActivity : AppCompatActivity() {
    private lateinit var dataStorage: AbstractDataRepository
    private lateinit var workItemsAdapter: WorkDataItemAdapter2
    private lateinit var noWorksView: TextView
    private lateinit var car: CarInfoEntity
    private var lastAddedItem: WorkInfoEntity? = null
    private lateinit var searchView: EditText
    private lateinit var activityScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val carDataItem = intent.getParcelableExtra(CAR_ITEM) as? CarInfoEntity?
        if (carDataItem == null) {
            finish()
            return
        }
        car = carDataItem

        setContentView(R.layout.work_list)
        setSupportActionBar(findViewById(R.id.toolbar))

        // DB
        activityScope = CoroutineScope(Dispatchers.Main + Job())
        dataStorage = RepositoryFactory().getRepository(this, activityScope)

        // Recycler view and adapter
        noWorksView = findViewById<TextView>(R.id.no_cars).apply { visibility = View.INVISIBLE }
        searchView = findViewById(R.id.searchView)

        workItemsAdapter = WorkDataItemAdapter2() { onCheckVisibility(it) }.apply {
            setEditWorkListener { dataItem: WorkInfoEntity, position: Int -> onEditWork(dataItem, position) }
            searchView.addTextChangedListener(object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    activityScope.launch {
                        filter(s, null, dataStorage.getWorkInfo(carDataItem.getId()))
                    }
                }
            })
        }
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            adapter = workItemsAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }

        // add new button
        val addButton = findViewById<FloatingActionButton>(R.id.fab)
        addButton.setOnClickListener { addWork(carDataItem) }

        // title
        val title = findViewById<TextView>(R.id.workTitle)
        title.text = "${carDataItem.producer} ${carDataItem.model} ${carDataItem.plateNumber}"

        //back
        findViewById<View>(R.id.backButton).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }


    private fun addWork(dataItem: CarInfoEntity) {
        val intent = Intent(this@WorkListActivity, EditWorkActivity::class.java)
        intent.putExtra(CAR_ITEM_ID, dataItem.getId())
        startActivityForResult(intent, REQUEST_CODE_WORKS)
    }

    private fun onEditWork(dataItem: WorkInfoEntity, position: Int) {
        val intent = Intent(this, EditWorkActivity::class.java)
        intent.putExtra(WORK_ITEM, dataItem)
        intent.putExtra(CAR_ITEM_ID, dataItem.carId)
        startActivityForResult(intent, REQUEST_CODE_WORKS)
    }

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        noWorksView.visibility = View.INVISIBLE
    } else {
        noWorksView.visibility = View.VISIBLE
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        val editableText = searchView.editableText
        activityScope.launch {
            workItemsAdapter.filter(editableText, lastAddedItem, dataStorage.getWorkInfo(car.getId()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == RESULT_OK && REQUEST_CODE_WORKS == requestCode) {
            val command = data.action
            if (CMD_ADD == command) {
                lastAddedItem = data.getParcelableExtra(WORK_ITEM)
            }
        }
    }

}