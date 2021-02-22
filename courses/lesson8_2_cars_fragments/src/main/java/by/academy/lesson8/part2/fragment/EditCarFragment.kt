package by.academy.lesson8.part2.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.academy.lesson8.part2.BuildConfig
import by.academy.lesson8.part2.CAR_ITEM
import by.academy.lesson8.part2.R
import by.academy.lesson8.part2.adapter.SharedViewModel
import by.academy.lesson8.part2.data.AbstractDataRepository
import by.academy.lesson8.part2.data.RepositoryFactory
import by.academy.lesson8.part2.entity.CarInfoEntity
import by.academy.utils.FilesAndImagesUtils.createImageFile
import by.academy.utils.LoggingTags.TAG_PHOTO
import by.academy.utils.checkPermission
import by.academy.utils.displayMessage
import by.academy.utils.notGivenPermission3
import by.academy.utils.setPhoto

//    For checking manual permissions for API level 23
private const val MY_PERMISSIONS_REQUEST_CAMERA = 22
private const val CAPTURE_IMAGE_REQUEST = 16

class EditCarFragment : Fragment(R.layout.car_add_edit) {
    private lateinit var dataStorage: AbstractDataRepository
    private lateinit var carFragmentManager: CarFragmentManager
    private lateinit var model: SharedViewModel
    private lateinit var photoBack: ImageView
    private lateinit var photo: ImageView
    private lateinit var ownerView: TextView
    private lateinit var producerView: TextView
    private lateinit var modelView: TextView
    private lateinit var plateNumberView: TextView

    private var mCurrentPhotoPath: String? = null

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // DB
        dataStorage = RepositoryFactory().getRepository(view.context)
        carFragmentManager = requireActivity() as CarFragmentManager
        model = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        model.setLastAddedCar(null)

        with(view) {
            ownerView = findViewById(R.id.viewTextOwnerName)
            producerView = findViewById(R.id.viewTextProducer)
            modelView = findViewById(R.id.viewTextModel)
            plateNumberView = findViewById(R.id.viewTextPlateNumber)

            photoBack = findViewById<View>(R.id.imagePreviewBackground) as ImageView
            photo = findViewById<View>(R.id.imagePreview) as ImageView
        }
        val removeButton = view.findViewById<View>(R.id.removeBUtton)
        val addButton = view.findViewById<View>(R.id.addBUtton)
        val backButton = view.findViewById<View>(R.id.backButton)
        val photoEditButton = view.findViewById<View>(R.id.imageEdit)

        backButton.setOnClickListener { backCancel() }
        photoEditButton.setOnClickListener(captureImageMultiVersion())

        val dataItem: CarInfoEntity? = getCarArgument()
        if (dataItem == null) {
            //  means a car adding
            view.findViewById<TextView>(R.id.toolbarTitle).setText(R.string.add_car)
            addButton.setOnClickListener { addCar(unbindData(0L)) }
            removeButton.visibility = View.INVISIBLE
        } else {
            view.findViewById<TextView>(R.id.toolbarTitle).setText(R.string.edit_car)
            bindData(dataItem)
            setPhoto()
            removeButton.setOnClickListener { removeCar(dataItem) }
            addButton.setOnClickListener { updateCar(unbindData(dataItem.getId())) }
        }
    }

    private fun getCarArgument(): CarInfoEntity? {
        return when (val theArguments = arguments) {
            null -> null
            else -> theArguments.getParcelable(CAR_ITEM)
        }
    }

    private fun bindData(dataItem: CarInfoEntity) {
        ownerView.text = dataItem.ownerName
        producerView.text = dataItem.producer
        modelView.text = dataItem.model
        plateNumberView.text = dataItem.plateNumber
        mCurrentPhotoPath = dataItem.imagePath
    }

    private fun unbindData(id: Long): CarInfoEntity {
        return CarInfoEntity(id,
                ownerView.text.toString(),
                producerView.text.toString(),
                modelView.text.toString(),
                plateNumberView.text.toString(),
                mCurrentPhotoPath
        )
    }

    //   -----------------   transitions
    private fun addCar(newDataItem: CarInfoEntity) {
        val newId = dataStorage.addCar(newDataItem)
        newDataItem.setId(newId);
        backWithData(newDataItem)
    }

    private fun updateCar(updatedDataItem: CarInfoEntity) {
        dataStorage.updateCar(updatedDataItem)
        carFragmentManager.closeFragment(this)
    }

    private fun removeCar(dataItem: CarInfoEntity) {
        dataStorage.removeCar(dataItem)
        carFragmentManager.closeFragment(this)
    }

    private fun backWithData(newDataItem: CarInfoEntity) {
        model.setLastAddedCar(newDataItem)
        carFragmentManager.simpleBack()
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

    /* TODO Денис, вопрос про доступ к активити из фрагментов
     Код ниже завязан на активити. FilesDir, checkPermission, packageManager
     Когда были отдельные активити для каждого экрана - это было логично
     Теперь активити одна.
     Этот код теперь должен быть здесь или его нужно перенести?

    Поскольку активити всегда явно присутствует, то можно легко вызывать его методы оттуда.
    Но правильно ли это при организации через фрагменты?
     */
    //--------------------   IMAGE   ------------------------
    @RequiresApi(Build.VERSION_CODES.N)
    private fun captureImageMultiVersion(): (View) -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            captureImage()
        } else {
            displayMessage(requireContext(), getString(R.string.camera_not_supported))
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun captureImage() {
        // check permission on storage and camera
        val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (checkPermission(this.requireActivity(), permissions, MY_PERMISSIONS_REQUEST_CAMERA)) {
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
                        displayMessage(requireContext(),
                                "${getString(R.string.permissions_not_given)}: $message")
                    }
            )
        }
    }

    private fun captureImageCameraIfPermitted() {
        Log.i(TAG_PHOTO, "captureImage: takePictureIntent")
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) == null) {
            displayMessage(requireContext(), "Null during resolveActivity method");
            return;
        }

        try {
            val photoFile = createImageFile(requireActivity().filesDir)
            // Continue only if the File was successfully created
            mCurrentPhotoPath = photoFile.absolutePath
            Log.i(TAG_PHOTO, "Photo path:$mCurrentPhotoPath")

            val photoURI = FileProvider.getUriForFile(requireContext(),
                    BuildConfig.APPLICATION_ID + ".provider", photoFile)
            Log.i(TAG_PHOTO, "Photo URI:$photoURI")
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)
        } catch (ex: Exception) {
            // Error occurred while creating the File
            Log.e(TAG_PHOTO, "exception ", ex)
            displayMessage(requireContext(), ex.message.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == RESULT_OK) {
            setPhoto()
        } else {
            displayMessage(requireContext(), getString(R.string.photo_capture_was_cancelled))
        }
    }

    private fun setPhoto() {
        setPhoto(mCurrentPhotoPath, photo, photoBack)
    }

}