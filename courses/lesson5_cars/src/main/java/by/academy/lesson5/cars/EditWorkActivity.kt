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
import by.academy.lesson5.cars.WorkListActivity.WORK_ITEM
import by.academy.lesson5.cars.data.DatabaseInfo.Companion.init
import by.academy.lesson5.cars.data.WorkInfoDAO
import by.academy.lesson5.cars.data.WorkInfoEntity
import by.academy.utils.dateFormat
import java.util.*

class EditWorkActivity : AppCompatActivity() {

    private lateinit var workDao: WorkInfoDAO

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.work_add_edit)

        if (!intent.hasExtra(WorkListActivity.CAR_ITEM_ID)) {
            finish()
            return
        }
        val carDataItemId = intent.getLongExtra(WorkListActivity.CAR_ITEM_ID, -1L)

        // DB
        val databaseInfo = init(this).value
        workDao = databaseInfo.getWorkInfoDAO()

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

        findViewById<View>(R.id.backButton).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        findViewById<View>(R.id.addBUtton).setOnClickListener {
            val data = Intent()

            val workBuilder = WorkBuilder()
            workBuilder.apply {
                workName = workNameView.text.toString()
                workDescription = descriptionView.text.toString()
                workCost = costView.text.toString()
                status = workStatusComponent.getStatus()
                this.carDataItemId = carDataItemId
            }

            if (workBuilder.isEmpty()) {
                displayMessage(this, getString(R.string.fields_must_be_filled))
                return@setOnClickListener
            }

            if (dataItem == null) {
                // add
                add(data, workBuilder, date)
            } else {
                // edit
                update(data, workBuilder, dataItem, carDataItemId)
            }
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun add(data: Intent, workBuilder: WorkBuilder, date: Date) {
        val newDataItem = WorkInfoEntity(0L,
                date,
                workBuilder.workName,
                workBuilder.status,
                workBuilder.workCost.toDouble(),
                workBuilder.workDescription
        )
        newDataItem.carId = workBuilder.carDataItemId
        data.apply {
            action = MainActivity.ADD
            val newId = workDao.add(newDataItem)
            newDataItem.id = newId;
            putExtra(WORK_ITEM, newDataItem)
        }
    }

    private fun update(data: Intent, workBuilder: WorkBuilder, dataItem: WorkInfoEntity, carDataItemId: Long) {
        val updatedDataItem = WorkInfoEntity(dataItem.id,
                dataItem.date,
                workBuilder.workName,
                workBuilder.status,
                workBuilder.workCost.toDouble(),
                workBuilder.workDescription
        )
        updatedDataItem.carId = carDataItemId

        data.apply {
            action = MainActivity.EDIT
            workDao.update(updatedDataItem)
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
        workDao.delete(dataItem)
        setResult(RESULT_OK, data)
        finish()

    }
}