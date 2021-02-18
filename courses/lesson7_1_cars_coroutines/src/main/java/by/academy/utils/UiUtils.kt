@file:JvmName("UiUtils")

package by.academy.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import by.academy.lesson7.part1.R

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
        imageView.setImageResource(android.R.color.transparent);
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


