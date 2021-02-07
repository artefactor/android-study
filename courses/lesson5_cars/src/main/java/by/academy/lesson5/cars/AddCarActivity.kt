package by.academy.lesson5.cars

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import by.academy.lesson5.cars.data.CarInfoEntity

class AddCarActivity : AppCompatActivity() {

    private var mCurrentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_add_edit)

        findViewById<View>(R.id.removeBUtton).visibility = View.INVISIBLE

        val ownerView = findViewById<TextView>(R.id.viewTextOwnerName)
        val producerView = findViewById<TextView>(R.id.viewTextProducer)
        val modelView = findViewById<TextView>(R.id.viewTextModel)
        val plateNumberView = findViewById<TextView>(R.id.viewTextPlateNumber)


        findViewById<View>(R.id.addBUtton).setOnClickListener {
            val dataItem = CarInfoEntity(0L,
                    ownerView.text.toString(),
                    producerView.text.toString(),
                    modelView.text.toString(),
                    plateNumberView.text.toString(),
                    mCurrentPhotoPath
            )

            val data = Intent().apply {
                action = MainActivity.ADD
                putExtra(MainActivity.ITEM, dataItem)
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