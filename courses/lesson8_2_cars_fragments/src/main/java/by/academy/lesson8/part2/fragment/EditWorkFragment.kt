package by.academy.lesson8.part2.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.academy.lesson8.part2.CAR_ITEM_ID
import by.academy.lesson8.part2.R
import by.academy.lesson8.part2.WORK_ITEM
import by.academy.lesson8.part2.adapter.SharedViewModel
import by.academy.lesson8.part2.data.AbstractDataRepository
import by.academy.lesson8.part2.data.RepositoryFactory
import by.academy.lesson8.part2.entity.WorkInfoEntity
import by.academy.lesson8.part2.helper.WS_IN_PROGRESS
import by.academy.lesson8.part2.helper.WorkBuilder
import by.academy.lesson8.part2.helper.WorkStatusComponent
import by.academy.utils.dateFormat
import by.academy.utils.displayMessage
import java.util.Date

class EditWorkFragment : Fragment(R.layout.work_add_edit) {
    private lateinit var model: SharedViewModel
    private lateinit var carFragmentManager: CarFragmentManager
    private lateinit var dataStorage: AbstractDataRepository
    private lateinit var date: Date

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("fragmentA", this.javaClass.simpleName + " onViewCreated")
        val carDataItemId = requireArguments().getLong(CAR_ITEM_ID)

        // DB
        dataStorage = RepositoryFactory().getRepository(view.context)
        carFragmentManager = requireActivity() as CarFragmentManager
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.setLastAddedWork(null)

        val removeButton = view.findViewById<View>(R.id.removeBUtton)
        val workNameView = view.findViewById<TextView>(R.id.viewTextWorkName)
        val costView = view.findViewById<TextView>(R.id.viewTextCost)
        val descriptionView = view.findViewById<TextView>(R.id.viewTextDescription)
        val workStatusComponent = WorkStatusComponent(view.findViewById(R.id.radioWorkStatus)).apply { init() }

        val dataItem: WorkInfoEntity? = requireArguments().getParcelable(WORK_ITEM)

        //  dataItem is null  -  means a work adding
        if (dataItem == null) {
            removeButton.visibility = View.INVISIBLE
            date = Date()
            workStatusComponent.setWorkStatus(WS_IN_PROGRESS)
        } else {
            view.findViewById<TextView>(R.id.toolbarTitle).text = dataItem.title
            bindData(workNameView, dataItem, descriptionView, costView, workStatusComponent)
            removeButton.setOnClickListener { removeWithConfirmationDialog(dataItem) }
        }

        view.findViewById<TextView>(R.id.viewTextDate).text = "${resources.getString(R.string.app_date)} ${dateFormat(date)}"
        view.findViewById<View>(R.id.backButton).setOnClickListener { backCancel()}
        view.findViewById<View>(R.id.addBUtton).setOnClickListener {
            val workBuilder = WorkBuilder()
            workBuilder.apply {
                workName = workNameView.text.toString()
                workDescription = descriptionView.text.toString()
                workCost = costView.text.toString()
                status = workStatusComponent.getStatusByButtonId()
                this.carDataItemId = carDataItemId
            }

            if (workBuilder.isEmpty()) {
                displayMessage(requireContext(), getString(R.string.fields_must_be_filled))
                return@setOnClickListener
            }

            if (dataItem == null) {
                addWork(unbindData(workBuilder, 0L, date))
            } else {
                updateWork(unbindData(workBuilder, dataItem.getId(), dataItem.date))
            }
        }
    }

    private fun bindData(workNameView: TextView, dataItem: WorkInfoEntity, descriptionView: TextView, costView: TextView, workStatusComponent: WorkStatusComponent) {
        workNameView.text = dataItem.title
        descriptionView.text = dataItem.description
        costView.text = dataItem.cost.toString()
        date = dataItem.date
        workStatusComponent.setWorkStatus(dataItem.status)
    }

    private fun unbindData(workBuilder: WorkBuilder, l: Long, date1: Date): WorkInfoEntity {
        val newDataItem = WorkInfoEntity(l,
                date1,
                workBuilder.workName,
                workBuilder.status,
                workBuilder.workCost.toDouble(),
                workBuilder.workDescription
        )
        newDataItem.carId = workBuilder.carDataItemId
        return newDataItem
    }

    //   -----------------   transitions
    private fun addWork(newDataItem: WorkInfoEntity) {
        val newId = dataStorage.addWork(newDataItem)
        newDataItem.setId(newId);
        model.setLastAddedWork(newDataItem)
        carFragmentManager.simpleBack()
    }

    private fun updateWork(updatedDataItem: WorkInfoEntity) {
        dataStorage.updateWork(updatedDataItem)
        carFragmentManager.closeFragment(this)
    }

    private fun removeWithConfirmationDialog(dataItem: WorkInfoEntity) {
        AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.remove_work))
                .setMessage(getString(R.string.remove_confirmation))
                .setPositiveButton(getString(R.string.remove)) { _, _ -> removeWork(dataItem) }
                .setNegativeButton(getString(R.string.No)) { dialog, _ -> dialog.cancel() }
                .setCancelable(true)
                .create()
                .show()
    }

    private fun removeWork(dataItem: WorkInfoEntity) {
        dataStorage.deleteWork(dataItem)
        carFragmentManager.closeFragment(this)
    }

    private fun backCancel() {
        carFragmentManager.onBackPressed()
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
    }

}