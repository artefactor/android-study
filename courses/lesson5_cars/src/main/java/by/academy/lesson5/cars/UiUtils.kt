package by.academy.lesson5.cars

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import by.academy.utils.CommonUtils
import by.academy.utils.LoggingTags

object UiUtils : CommonUtils() {

    fun setPhoto(imagePath: String?, imageView: ImageView, imageViewBack: ImageView) {
        if (imagePath == null) {
            imageView.visibility = View.VISIBLE
        } else {
            val color = ContextCompat.getColor(imageViewBack.context, R.color.teal_200)
            imageViewBack.setBackgroundColor(color)
            imageView.visibility = View.GONE
        }

        setImage(imageViewBack, imagePath)
    }

    fun setImage(imageView: ImageView, absolutePath: String?) {
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

    fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}

