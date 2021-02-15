package by.academy.lesson7.part1

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import by.academy.utils.FilesAndImagesUtils.appendLogFile
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.function.Supplier

const val APPLOG_LOG = "applog.log"
const val REQUEST_CODE = 21

const val WORK_ITEM = "workitem"
const val CAR_ITEM = "caritem"
const val CAR_ITEM_ID = "carId"

const val CMD_ADD = "add"
const val CMD_REMOVE = "remove"
const val CMD_EDIT = "edit"


class MainActivity : AppCompatActivity() {
    private lateinit var carItemsAdapter: CarDataItemAdapter2
    private lateinit var dataStorage: AbstractDataRepository
    private lateinit var noCarsView: View
    private var lastAddedItem: CarInfoEntity? = null
    private lateinit var searchView: EditText
    private lateinit var activityScope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // logging
        appendLogFile(applicationContext, APPLOG_LOG)

        // DB
        activityScope = CoroutineScope(Dispatchers.Main + Job())
        dataStorage = RepositoryFactory().getRepository(this, activityScope)

        // Recycler view and adapter
        noCarsView = findViewById<TextView>(R.id.no_cars).apply { visibility = View.INVISIBLE }
        searchView = findViewById(R.id.searchView)

        carItemsAdapter = CarDataItemAdapter2(arrayListOf()) { invisible: Boolean -> onCheckVisibility(invisible) }.apply {
            setEditCarListener { dataItem: CarInfoEntity, position: Int -> edit(dataItem, position) }
            setShowWorkListener { dataItem: CarInfoEntity, position: Int -> showWorks(dataItem, position) }
            addFilteringBy(searchView, getCarsSupplier())
        }
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            adapter = carItemsAdapter
            layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        }

        // add new button
        val addButton = findViewById<FloatingActionButton>(R.id.fab)
        addButton.setOnClickListener { add() }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel()
    }

    private fun add() {
        val intent = Intent(this, EditCarActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun edit(dataItem: CarInfoEntity, position: Int) {
        val intent = Intent(this@MainActivity, EditCarActivity::class.java)
        intent.putExtra(CAR_ITEM, dataItem)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun showWorks(dataItem: CarInfoEntity, position: Int) {
        val intent = Intent(this@MainActivity, WorkListActivity::class.java)
        intent.putExtra(CAR_ITEM, dataItem)
        startActivity(intent)
    }

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        noCarsView.visibility = View.INVISIBLE
    } else {
        noCarsView.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == RESULT_OK && REQUEST_CODE == requestCode) {
            val command = data.action
            if (CMD_ADD == command) {
                lastAddedItem = data.getParcelableExtra(CAR_ITEM)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        activityScope.launch {
            carItemsAdapter.filter(searchView.text, lastAddedItem, dataStorage.getAllCars())
        }
    }

    /* FIXME Денис. а будет ли такой подход работать?
        у меня в 5й работе была лямбда, которая на фильтр возвращала свежий список,
        здесь я постарался так же сделать.
        Намучался пару часов, но думаю, что не совсем правильный подход.
        можешь прокомментировать?
     */
    private fun getCarsSupplier(): Supplier<List<CarInfoEntity>>? {
        return Supplier<List<CarInfoEntity>> {
            val result: List<CarInfoEntity>
            runBlocking {
                result = dataStorage.getAllCars()
            }
            result
        }
    }

}