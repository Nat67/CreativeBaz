package com.example.creativebaz.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.example.creativebaz.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.loginbtn
import kotlinx.android.synthetic.main.activity_login.mail
import kotlinx.android.synthetic.main.activity_login.password
import kotlinx.android.synthetic.main.activity_login.registerbtn
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        registerbtn.setOnClickListener {
            val intento = Intent(this, RegisterActivity::class.java)
            startActivity(intento)
        }

        loginbtn.setOnClickListener {
            setLogin()
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(mail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                //showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }

    private fun setLogin(){
        if(validateLoginDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = mail.text.toString().trim{ it <= ' ' }
            val password: String = password.text.toString().trim{ it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->

                hideProgressDialog()

                if(task.isSuccessful) {
                    showErrorSnackBar("Ingresó correctamente", false)
                }else{
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
        }
    }


}