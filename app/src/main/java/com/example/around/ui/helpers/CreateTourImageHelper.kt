package com.example.around.ui.helpers

import android.net.Uri
import android.widget.ImageView

object CreateTourImageHelper {

    fun applyPreview(preview: ImageView?, uri: Uri?) {
        if (preview == null || uri == null) return

        preview.visibility = ImageView.VISIBLE
        preview.setImageURI(uri)
    }
}