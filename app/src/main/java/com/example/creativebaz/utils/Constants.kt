package com.example.creativebaz.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    const val USERS : String = "users"
    const val PRODUCTS : String = "products"

    const val CREATIVEBAZ_PREFS : String = "CreativeBazPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE = 2

    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MOBILE: String = "mobile"
    const val PROFESSION: String = "profession"
    const val BIO: String = "bio"
    const val NAME: String = "name"
    const val IMAGE: String = "image"
    const val PROFILE_COMPLETE: String = "profileCompleted"

    const val PRODUCT_IMAGE : String= "Product_Image"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"

    const val USER_ID : String = "user_id"

    const val EXTRA_PRODUCT_ID: String ="extra_product_id"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return  MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}