package by.academy.lesson5.cars

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import by.academy.lesson5.cars.UiUtils.dateFormat
import by.academy.lesson5.cars.data.WorkInfoEntity
import java.util.*

class EditWorkActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.work_add_edit)

        if (!intent.hasExtra(WorkListActivity.CAR_ITEM_ID)) {
            finish()
            return
        }
        val carDataItemId = intent.getLongExtra(WorkListActivity.CAR_ITEM_ID, -1L)
        val removeButton = findViewById<View>(R.id.removeBUtton)

        val workNameView = findViewById<TextView>(R.id.viewTextWorkName)
        val costView = findViewById<TextView>(R.id.viewTextCost)
        val descriptionView = findViewById<TextView>(R.id.viewTextDescription)

        val workStatusRadioGroup = findViewById<RadioGroup>(R.id.radioWorkStatus)

        val wStatusInProgress: RadioButton = workStatusRadioGroup.findViewById(R.id.workStatusInProgress)
        val wStatusCompleted: RadioButton = workStatusRadioGroup.findViewById(R.id.workStatusCompleted)
        val wStatusInPending: RadioButton = workStatusRadioGroup.findViewById(R.id.workStatusPending)

        val workStatusComponent = WorkStatusComponent(workStatusRadioGroup,
                wStatusInProgress,
                wStatusCompleted,
                wStatusInPending,
                resources
        )
        workStatusComponent.init();

        val dataItem: WorkInfoEntity? = intent.getParcelableExtra(WorkListActivity.WORK_ITEM)
        val date: Date

        //  dataItem is null  -  means a work adding
        if (dataItem == null) {
            removeButton.visibility = View.INVISIBLE
            date = Date()
            workStatusComponent.setWorkStatus(0)
        } else {
            findViewById<TextView>(R.id.toolbarTitle).text = dataItem.title

            workNameView.text = dataItem.title
            descriptionView.text = dataItem.description
            costView.text = dataItem.cost.toString()
            date = dataItem.date

            removeButton.setOnClickListener {
                removeWithConfirmationDialog(dataItem)
            }
            workStatusComponent.setWorkStatus(dataItem.status)
        }

        val dateFormat = dateFormat(date)

        findViewById<TextView>(R.id.viewTextDate).text = "${resources.getString(R.string.app_date)} $dateFormat"

        findViewById<View>(R.id.addBUtton).setOnClickListener {
            val data = Intent()

            val workName = workNameView.text.toString()
            val workDescription = descriptionView.text.toString()
            val workCost = costView.text.toString()
            val status = workStatusComponent.getStatus()


            if (workName.isEmpty() || workDescription.isEmpty() || workCost.isEmpty()) {
                UiUtils.displayMessage(this, getString(R.string.fields_must_be_filled))
                return@setOnClickListener
            }

            if (dataItem == null) {
                // add
                val newDataItem = WorkInfoEntity(0L,
                        date,
                        workName,
                        status,
                        workCost.toDouble(),
                        workDescription
                )
                newDataItem.carId = carDataItemId
                data.apply {
                    action = MainActivity.ADD
                    putExtra(WorkListActivity.WORK_ITEM, newDataItem)
                }
            } else {
                // edit
                val updatedDataItem = WorkInfoEntity(dataItem.id,
                        dataItem.date,
                        workName,
                        status,
                        workCost.toDouble(),
                        workDescription
                )
                updatedDataItem.carId = carDataItemId

                data.apply {
                    action = MainActivity.EDIT
                    putExtra(WorkListActivity.WORK_ITEM, updatedDataItem)
                }
            }
            setResult(RESULT_OK, data)
            finish()
        }
        findViewById<View>(R.id.backButton).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }


    private fun removeWithConfirmationDialog(dataItem: WorkInfoEntity) {
        AlertDialog.Builder(this)
                .setTitle(getString(R.string.remove_work))
                .setMessage(getString(R.string.remove_confirmation))
                .setPositiveButton(getString(R.string.remove)) { _, _ -> remove(dataItem) }
                .setNegativeButton(getString(R.string.No)) { dialog, _ -> dialog.cancel() }
                .setCancelable(true)
                .create()
                .show()
    }

    private fun remove(dataItem: WorkInfoEntity) {
        val data = Intent()
        data.action = MainActivity.REMOVE
        data.putExtra(WorkListActivity.WORK_ITEM, dataItem)
        setResult(RESULT_OK, data)
        finish()

    }
}