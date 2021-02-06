package by.academy.lesson5.cars

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import by.academy.lesson5.cars.data.CarInfoEntity


class EditCarActivity : AppCompatActivity() {

    private val REQUEST_CODE_PHOTO = 1
    lateinit var ivPhoto: ImageView
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_add_edit)

        val dataItem: CarInfoEntity? = intent.getParcelableExtra(MainActivity.ITEM)
        if (dataItem == null) {
            finish()
            return
        }
        findViewById<TextView>(R.id.toolbarTitle).setText(R.string.edit_car)

        val ownerView = findViewById<TextView>(R.id.viewTextOwnerName).apply { text = dataItem.ownerName }
        val producerView = findViewById<TextView>(R.id.viewTextProducer).apply { text = dataItem.producer }
        val modelView = findViewById<TextView>(R.id.viewTextModel).apply { text = dataItem.model }
        val plateNumberView = findViewById<TextView>(R.id.viewTextPlateNumber).apply { text = dataItem.plateNumber }

        ivPhoto = findViewById<View>(R.id.imagePreviewBackground) as ImageView
//        setPhoto(ivPhoto, dataItem.imagePath)

        findViewById<View>(R.id.removeBUtton).setOnClickListener {
            val data = Intent()
            data.action = MainActivity.REMOVE
            data.putExtra(MainActivity.ITEM, dataItem)
            setResult(RESULT_OK, data)
            finish()
        }

         var imagePath: String = ""

        findViewById<View>(R.id.addBUtton).setOnClickListener {
            val updatedDataItem = CarInfoEntity(dataItem.id,
                    ownerView.text.toString(),
                    producerView.text.toString(),
                    modelView.text.toString(),
                    plateNumberView.text.toString(),
                    imagePath)
            val data = Intent().apply {
                action = MainActivity.EDIT
                putExtra(MainActivity.ITEM, updatedDataItem)
            }
            setResult(RESULT_OK, data)
            finish()
        }

        findViewById<View>(R.id.backButton).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        findViewById<View>(R.id.imageEdit).setOnClickListener(function())
    }

    private fun function(): (View) -> Unit = {
        d()
    }

    private fun d() {

    }

    private fun setPhoto(ivPhoto: ImageView, imagePath: String?) {
        //        TODO("Not yet implemented")
        // photo
        imageUri = Uri.parse("/storage/emulated/0/DCIM/Camera/IMG_20210203_170116.jpg")
        val bitmap = BitmapFactory.decodeFile("/storage/emulated/0/DCIM/Camera/IMG_20210203_170116.jpg")
        Log.d("photo", "Photo uri: " + imageUri)
        ivPhoto.setImageBitmap(bitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
            в(data)
        }
    }


    private fun ы() {
    }

    private fun в(data: Intent?) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Toast.makeText(this@EditCarActivity, "GET_ACCOUNTS Denied",
                Toast.LENGTH_SHORT).show()

    }

}