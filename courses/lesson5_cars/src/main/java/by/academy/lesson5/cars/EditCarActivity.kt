package by.academy.lesson5.cars

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import by.academy.lesson5.cars.PermissionsHelper.checkPermission
import by.academy.lesson5.cars.PermissionsHelper.notGivenPermission3
import by.academy.lesson5.cars.UiUtils.displayMessage
import by.academy.lesson5.cars.UiUtils.setPhoto
import by.academy.lesson5.cars.data.CarInfoEntity
import by.academy.utils.FilesAndImagesUtils.createImageFile
import by.academy.utils.LoggingTags.TAG_PHOTO


class EditCarActivity : AppCompatActivity() {

    //    For checking manual permissions for API level 23
    private val MY_PERMISSIONS_REQUEST_CAMERA = 22
    private val CAPTURE_IMAGE_REQUEST = 16

    lateinit var photoBack: ImageView
    lateinit var photo: ImageView
    private var mCurrentPhotoPath: String? = null


    @RequiresApi(api = Build.VERSION_CODES.N)
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
        mCurrentPhotoPath = dataItem.imagePath

        photoBack = findViewById<View>(R.id.imagePreviewBackground) as ImageView
        photo = findViewById<View>(R.id.imagePreview) as ImageView
        setPhoto()

        findViewById<View>(R.id.removeBUtton).setOnClickListener {
            val data = Intent()
            data.action = MainActivity.REMOVE
            data.putExtra(MainActivity.ITEM, dataItem)
            setResult(RESULT_OK, data)
            finish()
        }

        findViewById<View>(R.id.addBUtton).setOnClickListener {
            val updatedDataItem = CarInfoEntity(dataItem.id,
                    ownerView.text.toString(),
                    producerView.text.toString(),
                    modelView.text.toString(),
                    plateNumberView.text.toString(),
                    mCurrentPhotoPath)

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

        findViewById<View>(R.id.imageEdit).setOnClickListener(captureImageMultiVersion())
    }


    //--------------------   IMAGE   ------------------------

    @RequiresApi(Build.VERSION_CODES.N)
    private fun captureImageMultiVersion(): (View) -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureImage()
        } else {
            displayMessage(baseContext, " Capture Image function for 4.4.4 and lower is not supported")
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun captureImage() {
        // check permission on storage and camera
        val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (checkPermission(this, permissions, MY_PERMISSIONS_REQUEST_CAMERA)) {
            Log.i(TAG_PHOTO, "requestPermissions " + permissions.contentToString())
            captureImageCameraIfPermitted()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.i(TAG_PHOTO, "onRequestPermissionsResult: " + requestCode + "; grantResults: " + grantResults.contentToString())
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            notGivenPermission3(grantResults, permissions,
                    this::captureImageCameraIfPermitted,
                    { message -> displayMessage(baseContext, message) }
            )
        }
    }

    private fun captureImageCameraIfPermitted() {
        Log.i(TAG_PHOTO, "captureImage: takePictureIntent")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        /*FIXME it  doesn't work.
            Денис, не понял про этот метод. Встречал его в нескольких примерах,
            на одной версии андроида у меня он работал, на другой - нет.
            Он от версии зависит?
            Или он вообще не нужен?
        */
//        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
//            displayMessage(getBaseContext(), "Null during resolveActivity method");
//            return;
//        }

        try {
            val photoFile = createImageFile(filesDir)
            // Continue only if the File was successfully created
            mCurrentPhotoPath = photoFile.absolutePath
            Log.i(TAG_PHOTO, "Photo path:$mCurrentPhotoPath")

            val photoURI = FileProvider.getUriForFile(applicationContext,
                    BuildConfig.APPLICATION_ID + ".provider", photoFile)
            Log.i(TAG_PHOTO, "Photo URI:$photoURI")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
        } catch (ex: Exception) {
            // Error occurred while creating the File
            Log.e(TAG_PHOTO, "exception ", ex)
            displayMessage(baseContext, ex.message.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            setPhoto()
        } else {
            displayMessage(baseContext, "Request cancelled or something went wrong.")
        }
    }

    private fun setPhoto() {
        setPhoto(mCurrentPhotoPath, photo, photoBack, resources)
    }

}