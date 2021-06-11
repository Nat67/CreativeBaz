package com.example.creativebaz.activities

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.creativebaz.R
import com.example.creativebaz.models.User
import com.example.creativebaz.utils.Constants
import kotlinx.android.synthetic.main.activity_user_profile.*
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import com.example.creativebaz.firestore.FirestoreClass
import com.example.creativebaz.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        edit_name.setText(mUserDetails.name)

        edit_mail.isEnabled = false
        edit_mail.setText(mUserDetails.email)

        user_photo.setOnClickListener(this@UserProfileActivity)
        save_btn.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.user_photo -> {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                    ){
                         Constants.showImageChooser(this)
                    } else{
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.save_btn -> {
                    showProgressDialog(resources.getString(R.string.please_wait))
                    FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)
                    /*
                    if(validateUserProfileDetails()){
                        //showErrorSnackBar("Tu información es correcta", false)
                        val userHashMap = HashMap<String, Any>()
                        val name = edit_name.text.toString().trim { it <= ' ' }
                        val mobileNumber = edit_mobile.text.toString().trim{ it <= ' '}
                        val bio = edit_bio.text.toString()
                        val profession = edit_profession.text.toString()

                        if (mobileNumber.isNotEmpty()){
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }

                        if (name.isNotEmpty()){
                            userHashMap[Constants.NAME] = name
                        }

                        if (bio.isNotEmpty()){
                            userHashMap[Constants.BIO] = bio
                        }

                        if (profession.isNotEmpty()){
                            userHashMap[Constants.PROFESSION] = profession
                        }

                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().updateUserProfileData(this, userHashMap)
                    }*/
                }
            }
        }
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this@UserProfileActivity,
        resources.getString(R.string.msg_profile_update_success), Toast.LENGTH_SHORT).show()

        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if(data != null){
                    try {
                        mSelectedImageFileUri = data.data!!
                        //user_photo.setImageURI(selectedImageFileUri)
                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, user_photo)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun validateUserProfileDetails(): Boolean{
        return when{
            TextUtils.isEmpty(edit_mobile.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString((R.string.err_msg_enter_mobile)), true)
                false
            }
            TextUtils.isEmpty(edit_profession.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString((R.string.err_msg_enter_profession)), true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String){
        hideProgressDialog()
        Toast.makeText(
            this@UserProfileActivity,
            "La imagen se subió correctamente. URL de la imagen es. $imageURL",
            Toast.LENGTH_SHORT
        ).show()
    }
}