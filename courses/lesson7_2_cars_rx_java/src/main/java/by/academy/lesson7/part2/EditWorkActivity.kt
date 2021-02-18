package by.academy.lesson7.part2

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import by.academy.lesson7.part2.data.AbstractDataRepository
import by.academy.lesson7.part2.data.RepositoryFactory
import by.academy.lesson7.part2.data.WorkInfoEntity
import by.academy.utils.dateFormat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.Date

class EditWorkActivity : AppCompatActivity() {

    private lateinit var dataStorage: AbstractDataRepository

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!intent.hasExtra(CAR_ITEM_ID)) {
            finish()
            return
        }
        val carDataItemId = intent.getLongExtra(CAR_ITEM_ID, -1L)
        setContentView(R.layout.work_add_edit)

        // DB
        dataStorage = RepositoryFactory().getRepository(this)

        val removeButton = findViewById<View>(R.id.removeBUtton)

        val workNameView = findViewById<TextView>(R.id.viewTextWorkName)
        val costView = findViewById<TextView>(R.id.viewTextCost)
        val descriptionView = findViewById<TextView>(R.id.viewTextDescription)

        val workStatusComponent = WorkStatusComponent(findViewById(R.id.radioWorkStatus)).apply { init() }

        val dataItem: WorkInfoEntity? = intent.getParcelableExtra(WORK_ITEM)
        val date: Date

        //  dataItem is null  -  means a work adding
        if (dataItem == null) {
            removeButton.visibility = View.INVISIBLE
            date = Date()
            workStatusComponent.setWorkStatus(WS_IN_PROGRESS)
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

        findViewById<TextView>(R.id.viewTextDate).text = "${resources.getString(R.string.app_date)} ${dateFormat(date)}"

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
                add(data, workBuilder, date)
            } else {
                update(data, workBuilder, dataItem, carDataItemId)
            }
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

        dataStorage.addWork(newDataItem)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { newId ->
                    data.action = CMD_ADD
                    newDataItem.setId(newId)
                    data.putExtra(WORK_ITEM, newDataItem)
                    setResult(RESULT_OK, data)
                    finish()
                }
    }

    private fun update(data: Intent, workBuilder: WorkBuilder, dataItem: WorkInfoEntity, carDataItemId: Long) {
        val updatedDataItem = WorkInfoEntity(dataItem.getId(),
                dataItem.date,
                workBuilder.workName,
                workBuilder.status,
                workBuilder.workCost.toDouble(),
                workBuilder.workDescription
        )
        updatedDataItem.carId = carDataItemId

        dataStorage.updateWork(updatedDataItem)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    data.action = CMD_EDIT
                    setResult(RESULT_OK, data)
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
        data.action = CMD_REMOVE
        dataStorage.deleteWork(dataItem)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    setResult(RESULT_OK, data)
                    finish()
                }
    }
}