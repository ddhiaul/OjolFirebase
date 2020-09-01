package com.aulia.idn.ojolfirebase.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aulia.idn.ojolfirebase.MainActivity
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.utils.Constant
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_authentification_hp.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class AuthentificationHpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentification_hp)

        //todo 13
        val key = intent.getStringExtra(Constant.key)
        val database = FirebaseDatabase.getInstance()
        val myref = database.getReference(Constant.tb_user)

        //update realtime database
        tv_submit_auth.onClick {
            if (et_auth_no_hp.text.toString().isNotEmpty()){
                myref.child(key).child("hp")
                    .setValue(et_auth_no_hp.text.toString())
                startActivity<MainActivity>()
            }
            else toast("tidak boleh kosong")
        }
    }
}