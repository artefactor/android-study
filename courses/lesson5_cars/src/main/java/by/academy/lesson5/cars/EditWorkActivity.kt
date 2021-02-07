package by.academy.lesson5.cars

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import by.academy.lesson5.cars.data.CarInfoEntity
import by.academy.lesson5.cars.data.WorkInfoEntity
import java.util.*

class EditWorkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.work_add_edit)

        if (!intent.hasExtra(WorkListActivity.CAR_ITEM_ID)) {
            finish()
            return
        }
        val carDataItemId: Long = intent.getLongExtra(WorkListActivity.CAR_ITEM_ID, -1L)
        findViewById<View>(R.id.removeBUtton).visibility = View.INVISIBLE

        val workNameView = findViewById<TextView>(R.id.viewTextWorkName)
        val costView = findViewById<TextView>(R.id.viewTextCost)
        val descriptionView = findViewById<TextView>(R.id.viewTextDescription)


        findViewById<View>(R.id.addBUtton).setOnClickListener {

            val workName = workNameView.text.toString()
            val workDescription = descriptionView.text.toString()
            val workCost = costView.text.toString()

            if (workName.isEmpty() || workDescription.isEmpty() || workCost.isEmpty()) {
                UiUtils.displayMessage(this, "Fields can't be empty")
                //                TODO uncomment
//                return@setOnClickListener
            }
            val dataItem = WorkInfoEntity(0L,
                    Date(),
                    workName,
                    1, //TODO
                    workCost.toDouble(),
                    workDescription
            )
            dataItem.carId = carDataItemId
            val data = Intent().apply {
                action = MainActivity.ADD
                putExtra(WorkListActivity.WORK_ITEM, dataItem)
            }
            setResult(RESULT_OK, data)
            finish()
        }
        findViewById<View>(R.id.backButton).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}