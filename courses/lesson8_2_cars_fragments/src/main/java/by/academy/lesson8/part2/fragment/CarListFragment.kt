package by.academy.lesson8.part2.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.academy.lesson8.part2.CAR_ITEM
import by.academy.lesson8.part2.R
import by.academy.lesson8.part2.adapter.CarDataItemAdapter2
import by.academy.lesson8.part2.adapter.SharedViewModel
import by.academy.lesson8.part2.data.AbstractDataRepository
import by.academy.lesson8.part2.data.RepositoryFactory
import by.academy.lesson8.part2.entity.CarInfoEntity
import by.academy.utils.TextWatcherAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CarListFragment : Fragment(R.layout.car_main) {
    private lateinit var model: SharedViewModel
    private lateinit var carFragmentManager: CarFragmentManager
    private lateinit var carItemsAdapter: CarDataItemAdapter2
    private lateinit var dataStorage: AbstractDataRepository
    private lateinit var noCarsView: View
    private var lastAddedItem: CarInfoEntity? = null
    private lateinit var searchView: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carFragmentManager = requireActivity() as CarFragmentManager

        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.lastAddedCar.observe(viewLifecycleOwner, { lastAddedItem ->
            carItemsAdapter.filter(searchView.text, lastAddedItem, dataStorage.getAllCars())
        })

        with(view) {
            // вопрос про тулбар записал отдельно в другом месте
//            setSupportActionBar(findViewById(R.id.toolbar))

            // Recycler view and adapter
            noCarsView = findViewById<TextView>(R.id.no_cars).apply { visibility = View.INVISIBLE }
            searchView = findViewById(R.id.searchView)
            val possibleSavedFilterString = savedInstanceState?.getString("FILTER_VALUE")
            searchView.setText(possibleSavedFilterString, TextView.BufferType.EDITABLE)

        }
//        // DB
        dataStorage = RepositoryFactory().getRepository(view.context)

        carItemsAdapter = CarDataItemAdapter2 { invisible: Boolean -> onCheckVisibility(invisible) }.apply {
            setEditCarListener { dataItem: CarInfoEntity, position: Int -> edit(dataItem, position) }
            setShowWorkListener { dataItem: CarInfoEntity, position: Int -> showWorks(dataItem, position) }
            searchView.addTextChangedListener(object : TextWatcherAdapter() {
                override fun afterTextChanged(s: Editable) {
                    filter(s, null, dataStorage.getAllCars())
                }
            })
        }

        with(view) {
            findViewById<RecyclerView>(R.id.recyclerView).apply {
                adapter = carItemsAdapter
                layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
            }

            // add new button
            val addButton = findViewById<FloatingActionButton>(R.id.fab)
            addButton.setOnClickListener { add() }
        }
    }

    //   -----------------   transitions
    private fun add() {
        carFragmentManager.openFragment(this, EditCarFragment())
    }

    private fun edit(dataItem: CarInfoEntity, position: Int) {
        val bundle = bundleOf(CAR_ITEM to dataItem)
        carFragmentManager.openFragment(this, EditCarFragment::class.java, bundle)
    }

    private fun showWorks(dataItem: CarInfoEntity, position: Int) {
        val bundle = bundleOf(CAR_ITEM to dataItem)
        carFragmentManager.openFragment(this, WorkListFragment::class.java, bundle)
    }

    private fun onCheckVisibility(invisible: Boolean) = if (invisible) {
        noCarsView.visibility = View.INVISIBLE
    } else {
        noCarsView.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        Log.i("fragmentA", this.javaClass.simpleName + " onResume")
        carItemsAdapter.filter(searchView.text, lastAddedItem, dataStorage.getAllCars())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("FILTER_VALUE", searchView.text.toString())
        Log.i("fragmentA", this.javaClass.simpleName + " onSaveInstanceState: " + searchView.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val string = savedInstanceState?.getString("FILTER_VALUE")
        Log.i("fragmentA", this.javaClass.simpleName + " onViewStateRestored: " + string )
    }

}