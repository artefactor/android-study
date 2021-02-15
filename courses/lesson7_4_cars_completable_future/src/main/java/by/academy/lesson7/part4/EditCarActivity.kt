package by.academy.lesson7.part4

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
import by.academy.lesson7.part4.data.AbstractDataRepository
import by.academy.lesson7.part4.data.CarInfoEntity
import by.academy.lesson7.part4.data.RepositoryFactory
import by.academy.utils.FilesAndImagesUtils.createImageFile
import by.academy.utils.LoggingTags.TAG_PHOTO
import by.academy.utils.checkPermission
import by.academy.utils.displayMessage
import by.academy.utils.notGivenPermission3
import by.academy.utils.setPhoto

//    For checking manual permissions for API level 23
private const val MY_PERMISSIONS_REQUEST_CAMERA = 22
private const val CAPTURE_IMAGE_REQUEST = 16

class EditCarActivity : AppCompatActivity() {

    private lateinit var photoBack: ImageView
    private lateinit var photo: ImageView
    private lateinit var ownerView: TextView
    private lateinit var producerView: TextView
    private lateinit var modelView: TextView
    private lateinit var plateNumberView: TextView

    private var mCurrentPhotoPath: String? = null

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.car_add_edit)


        // DB
        val dataStorage = RepositoryFactory().getRepository(this)
        
        ownerView = findViewById(R.id.viewTextOwnerName)
        producerView = findViewById(R.id.viewTextProducer)
        modelView = findViewById(R.id.viewTextModel)
        plateNumberView = findViewById(R.id.viewTextPlateNumber)

        photoBack = findViewById<View>(R.id.imagePreviewBackground) as ImageView
        photo = findViewById<View>(R.id.imagePreview) as ImageView
        val removeButton = findViewById<View>(R.id.removeBUtton)

        val dataItem: CarInfoEntity? = intent.getParcelableExtra(CAR_ITEM) as? CarInfoEntity?

        //  dataItem is null  -  means a car adding
        if (dataItem == null) {
            removeButton.visibility = View.INVISIBLE
        } else {
            findViewById<TextView>(R.id.toolbarTitle).setText(R.string.edit_car)

            ownerView.text = dataItem.ownerName
            producerView.text = dataItem.producer
            modelView.text = dataItem.model
            plateNumberView.text = dataItem.plateNumber

            mCurrentPhotoPath = dataItem.imagePath

            setPhoto()

            removeButton.setOnClickListener {
                dataStorage.removeCar(dataItem)
                val data = Intent()
                data.action = CMD_REMOVE
                setResult(RESULT_OK, data)
                finish()
            }
        }

        findViewById<View>(R.id.addBUtton).setOnClickListener {
            val data = Intent()
            if (dataItem == null) {
                addCar(data, dataStorage)
            } else {
                updateCar(dataItem, data, dataStorage)
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

    private fun addCar(data: Intent, dataStorage: AbstractDataRepository) {
        // add
        val newDataItem = CarInfoEntity(0L,
                ownerView.text.toString(),
                producerView.text.toString(),
                modelView.text.toString(),
                plateNumberView.text.toString(),
                mCurrentPhotoPath
        )
        val newId = dataStorage.addCar(newDataItem)

        data.apply {
            action = CMD_ADD
            newDataItem.setId(newId);
            putExtra(CAR_ITEM, newDataItem)
        }
    }

    private fun updateCar(dataItem: CarInfoEntity, data: Intent, dataStorage: AbstractDataRepository) {
        // edit
        val updatedDataItem = CarInfoEntity(dataItem.getId(),
                ownerView.text.toString(),
                producerView.text.toString(),
                modelView.text.toString(),
                plateNumberView.text.toString(),
                mCurrentPhotoPath)
        dataStorage.updateCar(updatedDataItem)

        data.apply {
            action = CMD_EDIT
        }
    }


    //--------------------   IMAGE   ------------------------

    @RequiresApi(Build.VERSION_CODES.N)
    private fun captureImageMultiVersion(): (View) -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureImage()
        } else {
            displayMessage(baseContext, getString(R.string.camera_not_supported))
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
                    { message ->
                        displayMessage(baseContext,
                                "${getString(R.string.permissions_not_given)}: $message")
                    }
            )
        }
    }

    private fun captureImageCameraIfPermitted() {
        Log.i(TAG_PHOTO, "captureImage: takePictureIntent")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          if (takePictureIntent.resolveActivity(packageManager) == null) {
            displayMessage(baseContext, "Null during resolveActivity method");
            return;
        }

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
            displayMessage(baseContext, getString(R.string.photo_capture_was_cancelled))
        }
    }

    private fun setPhoto() {
        setPhoto(mCurrentPhotoPath, photo, photoBack)
    }

}