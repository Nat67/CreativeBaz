package com.example.creativebaz.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.creativebaz.R
import com.example.creativebaz.firestore.FirestoreClass
import com.example.creativebaz.models.Product
import com.example.creativebaz.utils.Constants
import com.example.creativebaz.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {

    private var mSelectedImageFileURI: Uri? = null
    private var mProductImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionBar()

        iv_add_update_product.setOnClickListener(this)
        btn_submit_add_product.setOnClickListener(this);
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        toolbar_add_product_activity.setNavigationOnClickListener {onBackPressed()}
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.iv_add_update_product -> {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this@AddProductActivity)
                    } else{
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }

                R.id.btn_submit_add_product -> {
                    if(validateProductDetails()){
                        showErrorSnackBar("Los detalles del producto se han agregado correctamente", false)
                        uploadProductImage()
                    }
                }
            }
        }
    }


    private fun validateProductDetails():Boolean{
        return when{
            mSelectedImageFileURI == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_price), true)
                false
            }


            TextUtils.isEmpty(et_product_quantity.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_quantity), true)
                false
            }

            TextUtils.isEmpty(et_product_category.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_category), true)
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Constants.showImageChooser(this)
        }else {
            Toast.makeText(this, resources.getString(R.string.read_storage_permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileURI, Constants.PRODUCT_IMAGE)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data != null){
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_edit_24))

                    mSelectedImageFileURI = data.data!!

                    try {
                        GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!, iv_product_image)
                    } catch(e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(
                this@AddProductActivity,
                resources.getString(R.string.product_upload_success_message),
                Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun imageUploadSuccess(imageURL: String){
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    private fun uploadProductDetails(){
        val username = this.getSharedPreferences(Constants.CREATIVEBAZ_PREFS, Context.MODE_PRIVATE).getString(Constants.LOGGED_IN_USERNAME, "")!!
        val product = Product(
                FirestoreClass().getCurrentUserId(),
                username,
                et_product_title.text.toString().trim{it <= ' '},
                et_product_price.text.toString().trim{it <= ' '},
                et_product_description.text.toString().trim{it <= ' '},
                et_product_quantity.text.toString().trim{it <= ' '},
                et_product_category.text.toString().trim{it <= ' '},
                mProductImageURL
        )

        FirestoreClass().registerProduct(this, product)
    }

}