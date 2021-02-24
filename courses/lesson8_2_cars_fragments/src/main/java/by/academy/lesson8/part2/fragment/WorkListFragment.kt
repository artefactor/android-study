package by.academy.lesson8.part2.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.academy.lesson8.part2.CAR_ITEM
import by.academy.lesson8.part2.CAR_ITEM_ID
import by.academy.lesson8.part2.R
import by.academy.lesson8.part2.WORK_ITEM
import by.academy.lesson8.part2.adapter.SharedViewModel
import by.academy.lesson8.part2.adapter.WorkDataItemAdapter2
import by.academy.lesson8.part2.data.*
import by.academy.lesson8.part2.entity.CarInfoEntity
import by.academy.lesson8.part2.entity.WorkInfoEntity
import by.academy.utils.TextWatcherAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class WorkListFragment : Fragment(R.layout.work_list) {
    private lateinit var carFragmentManager: CarFragmentManager
    private lateinit var dataStorage: AbstractDataRepository
    private lateinit var workItemsAdapter: WorkDataItemAdapter2
    private lateinit var noWorksView: TextView
    private lateinit var carDataItem: CarInfoEntity
    private var lastAddedItem: WorkInfoEntity? = null
    private lateinit var searchView: EditText


    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("fragmentA", this.javaClass.simpleName + " onViewCreated")

        // DB
        dataStorage = RepositoryFactory().getRepository(view.context)
        carFragmentManager = requireActivity() as CarFragmentManager
        carDataItem = requireArguments().getParcelable(CAR_ITEM)!!
        val model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.lastAddedWork.observe(viewLifecycleOwner, { lastAddedItem ->
            workItemsAdapter.filter(searchView.text, lastAddedItem, dataStorage.getWorkInfo(carDataItem.getId()))
        })

        with(view) {
//            setSupportActionBar(findViewById(R.id.toolbar))
            // Recycler view and adapter
            noWorksView = findViewById<TextView>(R.id.no_cars).apply { visibility = View.INVISIBLE }
            searchView = findViewById(R.id.searchView)

            workItemsAdapter = WorkDataItemAdapter2(this@WorkListFragment::onCheckVisibility).apply {
                setEditWorkListener { dataItem: WorkInfoEntity, position: Int -> onEditWork(dataItem, position) }
                searchView.addTextChangedListener(object : TextWatcherAdapter() {
                    override fun afterTextChanged(s: Editable) {
                        filter(s, null, dataStorage.getWorkInfo(carDataItem.getId()))
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
            findViewById<View>(R.id.backButton).setOnClickListener { backCancel() }
        }
    }

    //   -----------------   transitions
    private fun addWork(dataItem: CarInfoEntity) {
        val bundle = bundleOf(CAR_ITEM_ID to dataItem.getId())
        carFragmentManager.openFragment(this, EditWorkFragment::class.java, bundle)
    }

    private fun onEditWork(dataItem: WorkInfoEntity, position: Int) {
        val bundle = bundleOf(
                CAR_ITEM_ID to carDataItem.getId(),
                WORK_ITEM to dataItem
        )
        carFragmentManager.openFragment(this, EditWorkFragment::class.java, bundle)
    }

    private fun backCancel() {
        carFragmentManager.onBackPressed()
    }

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        noWorksView.visibility = View.INVISIBLE
    } else {
        noWorksView.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        Log.i("fragmentA", this.javaClass.simpleName + " onResume")
        val editableText = searchView.text
        workItemsAdapter.filter(editableText, lastAddedItem, dataStorage.getWorkInfo(carDataItem.getId()))
    }

    //-------------------- logging
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i("fragmentA", this.javaClass.simpleName + " onAttach")
    }

    override fun onPause() {
        super.onPause()
        Log.i("fragmentA", this.javaClass.simpleName + " onPause")
    }

}