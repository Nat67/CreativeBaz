package com.example.creativebaz.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    // Firebase Constants
    // This is used for the collection name for USERS.
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val CART_ITEMS: String = "cart_items"
    const val ADDRESSES: String = "addresses"
    const val ORDERS: String = "orders"
    const val SOLD_PRODUCTS: String = "sold_products"
    const val USER_ID : String = "user_id"
    const val PRODUCT_ID: String = "product_id"

    const val CREATIVEBAZ_PREFS: String = "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    // Intent extra constants.
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_MY_ORDER_DETAILS: String = "extra_my_order_details"
    const val EXTRA_SOLD_PRODUCT_DETAILS: String = "extra_sold_product_details"

    //A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult in the Base Activity.
    const val READ_STORAGE_PERMISSION_CODE = 2

    // A unique code of image selection from Phone Storage.
    const val PICK_IMAGE_REQUEST_CODE = 2
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    // Constant variables for Gender

    const val DEFAULT_CART_QUANTITY: String = "1"

    // Firebase database field names
    const val MOBILE: String = "mobile"
    const val PROFESSION: String = "profession"
    const val BIO: String = "bio"
    const val NAME: String = "name"
    const val IMAGE: String = "image"
    const val PROFILE_COMPLETE: String = "profileCompleted"

    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    const val PRODUCT_IMAGE: String = "Product_Image"

    const val CART_QUANTITY: String = "cart_quantity"

    const val STOCK_QUANTITY: String = "stock_quantity"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"



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