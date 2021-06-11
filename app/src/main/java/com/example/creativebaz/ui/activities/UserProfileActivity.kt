package com.example.creativebaz.ui.activities

import android.content.pm.PackageManager
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
    private var mUserProfileImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        edit_name.setText(mUserDetails.name)

        edit_mail.isEnabled = false
        edit_mail.setText(mUserDetails.email)

        if (mUserDetails.profileCompleted == 0){
            editTitle.text = resources.getString(R.string.title_complete_profile)
            edit_name.setText(mUserDetails.name)

            edit_mail.isEnabled = false

        } else{
            setupActionBar()
            editTitle.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, user_photo)

            edit_bio.setText(mUserDetails.bio.toString())
            edit_profession.setText(mUserDetails.profession.toString())

            if(mUserDetails.mobile != 0L){
                edit_mobile.setText(mUserDetails.mobile.toString())
            }
        }

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


                    if(validateUserProfileDetails()){
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if(mSelectedImageFileUri != null){
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                        }else{
                          updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()
        val name = edit_name.text.toString().trim { it <= ' ' }
        val mobileNumber = edit_mobile.text.toString().trim{ it <= ' '}
        val bio = edit_bio.text.toString()
        val profession = edit_profession.text.toString().trim{ it <= ' '}

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (name.isNotEmpty() && name != mUserDetails.name){
            userHashMap[Constants.NAME] = name
        }

        if (bio.isNotEmpty() && bio != mUserDetails.bio){
            userHashMap[Constants.BIO] = bio
        }

        if (profession.isNotEmpty() && profession != mUserDetails.profession){
            userHashMap[Constants.PROFESSION] = profession
        }
        userHashMap[Constants.PROFILE_COMPLETE] =1

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this@UserProfileActivity,
        resources.getString(R.string.msg_profile_update_success), Toast.LENGTH_SHORT).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
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
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back)
        }

        toolbar_user_profile_activity.setNavigationOnClickListener {onBackPressed()}
    }
}