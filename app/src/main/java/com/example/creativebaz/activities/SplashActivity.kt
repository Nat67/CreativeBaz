package com.example.creativebaz.activities

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.example.creativebaz.R
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        progressBar.max = 1000
        val current_progress = 1000
        ObjectAnimator.ofInt(progressBar, "progress", current_progress).setDuration(3000).start()

        Handler().postDelayed({
            val intento = Intent(this, MainActivity::class.java)
            startActivity(intento)
            finish() }, 3500)

        /*val typeface: Typeface = Typeface.createFromAsset(assets, "Cinzel-Medium.ttf")
        AppTitle.typeface = typeface*/

    }



}