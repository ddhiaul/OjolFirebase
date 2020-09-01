package com.aulia.idn.ojolfirebase.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.aulia.idn.ojolfirebase.MainActivity
import com.aulia.idn.ojolfirebase.R
import com.google.firebase.auth.FirebaseAuth
import org.jetbrains.anko.startActivity

class SplashScreenActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //todo 12
        auth = FirebaseAuth.getInstance()

        Handler().postDelayed(Runnable {
            if (auth?.currentUser?.displayName != null) {
                startActivity<MainActivity>()
            } else startActivity<RegisterActivity>()
        }, 3000)
    }
}