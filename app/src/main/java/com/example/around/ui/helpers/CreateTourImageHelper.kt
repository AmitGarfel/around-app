package com.example.around.ui.helpers

import android.net.Uri
import android.view.View
import android.widget.ImageView

object CreateTourImageHelper {

    fun applyPreview(preview: ImageView?, uri: Uri?) {
        preview?.let { imageView ->
            uri?.let {
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(it)
            }
        }
    }
}