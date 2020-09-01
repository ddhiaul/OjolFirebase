package com.aulia.idn.ojolfirebase.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.model.Users
import com.aulia.idn.ojolfirebase.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity

class RegisterActivity : AppCompatActivity() {

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        btn_reg.onClick {
            if (et_email_reg.text.isNotEmpty() &&
                    et_name_reg.text.isNotEmpty() &&
                    et_hp_reg.text.isNotEmpty() &&
                    et_password_reg.text.isNotEmpty() &&
                    et_confirm_password_reg.text.isNotEmpty()
            ){
                authUserSignUp(
                    et_email_reg.text.toString(),
                    et_password_reg.text.toString()
                )
            }
        }
    }
    //proses authentification

    private fun authUserSignUp(email : String, pass: String):Boolean?{
        auth = FirebaseAuth.getInstance()

        var status: Boolean? = null
        val TAG = "tag"

        auth?.createUserWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    if(insertUser(
                            et_name_reg.text.toString(),
                            et_email_reg.text.toString(),
                            et_hp_reg.text.toString(),
                            task.result?.user!!
                        )){
                        startActivity<LoginActivity>()
                    }
                }else{
                    status = false
                }
            }
        return status
    }

    //proses nmbahin data user k realtime database
    fun insertUser(
        name: String,
        email: String,
        hp: String,
        users: FirebaseUser
    ): Boolean {
        var user = Users()
        user.uid = users.uid
        user.name = name
        user.email = email
        user.hp = hp

        val databate = FirebaseDatabase.getInstance()
        //id yg msk k database
        var key = databate.reference.push().key
        //nama table
        val myRef = databate.getReference(Constant.tb_user)
        //nyimpen k database
        myRef.child(key!!).setValue(user)

        return true

    }
}