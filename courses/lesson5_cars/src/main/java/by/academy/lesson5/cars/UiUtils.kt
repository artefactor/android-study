package by.academy.lesson5.cars

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import by.academy.utils.LoggingTags
import java.util.Date

object UiUtils {

    fun setPhotoAndInit(imagePath: String?, imageView: ImageView, imageViewBack: ImageView, viewEdit: View, resources: Resources) {
        if (imagePath == null) {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.ic_baseline_camera_alt_24)
            imageView.setBackgroundColor(resources.getColor(R.color.purple_200))
            viewEdit.setBackgroundColor(resources.getColor(R.color.purple_200))
        } else {
            viewEdit.setBackgroundColor(resources.getColor(R.color.teal_200))
            imageViewBack.setBackgroundColor(resources.getColor(R.color.teal_200))
            imageView.visibility = View.GONE
        }

        setImage(imageViewBack, imagePath)
    }

    fun setPhoto(imagePath: String?, imageView: ImageView, imageViewBack: ImageView, resources: Resources) {
        if (imagePath == null) {
            imageView.visibility = View.VISIBLE
        } else {
            imageViewBack.setBackgroundColor(resources.getColor(R.color.teal_200))
            imageView.visibility = View.GONE
        }

        setImage(imageViewBack, imagePath)
    }

    private fun setImage(imageView: ImageView, absolutePath: String?) {
        if (absolutePath == null) {
            return
        }
        val myBitmap = BitmapFactory.decodeFile(absolutePath)
        if (myBitmap != null) {
            val width = myBitmap.width
            val height = myBitmap.height
            Log.i(LoggingTags.TAG_PHOTO, "absolutePath: $absolutePath")
            Log.i(LoggingTags.TAG_PHOTO, "image size: $width;$height")
            imageView.setImageBitmap(myBitmap)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun dateFormat(date: Date): String? = SimpleDateFormat.getDateInstance().format(date)

    fun formatMoney(cost: Double): String = "$cost $"

    fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}

