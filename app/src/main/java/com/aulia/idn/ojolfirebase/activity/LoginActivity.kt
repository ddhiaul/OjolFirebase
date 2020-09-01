package com.aulia.idn.ojolfirebase.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.aulia.idn.ojolfirebase.MainActivity
import com.aulia.idn.ojolfirebase.R
import com.aulia.idn.ojolfirebase.model.Users
import com.aulia.idn.ojolfirebase.utils.Constant
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    var googleSigninClient: GoogleSignInClient? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        //todo 11
        btn_signup_with_google.onClick {
            logIn()
        }

        tv_signup_link.onClick {
            startActivity<RegisterActivity>()
        }
        btn_login.onClick {
            if (et_email_login.text.isNotEmpty() &&
                    et_password_login.text.isNotEmpty()
            ){
                authUserLogin(
                    et_email_login.text.toString(),
                    et_password_login.text.toString()
                )
            }
        }
    }

    //todo 10
    //authentification login email pw
    private fun authUserLogin(email: String, pass:String){
        var status : Boolean? = null

        auth?.signInWithEmailAndPassword(email, pass)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    startActivity<MainActivity>()
                    finish()
                }else{
                    toast("login failed")
                    Log.e("error", "message")
                }
            }
    }
    //todo 5
    //req login gmail
    private fun logIn(){
        val gson = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSigninClient = GoogleSignIn.getClient(this, gson)

        val loginIntent = googleSigninClient?.signInIntent
        startActivityForResult(loginIntent, 4)
    }
    //hasil req login google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 4){
            val task = GoogleSignIn
                .getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            }catch (e: ApiException){

            }
        }
    }
    //todo 7
    //auth firebase login
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        var uid = String()
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) {task ->
                if (task.isSuccessful){
                    val user = auth?.currentUser
                    checkDatabase(task.result?.user?.uid, account)
                    uid = user?.uid.toString()
                } else {

                }
            }
    }

    //todo 8
    //check database
    private fun checkDatabase(uid: String?, account: GoogleSignInAccount?) {

        val database = FirebaseDatabase.getInstance()
        val myref = database.getReference(Constant.tb_user)
        val query = myref.orderByChild("uid").equalTo(auth?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    startActivity<MainActivity>()
                } else{
                    account?.displayName?.let {
                        account.email?.let { it1 ->
                            insertUser(it, it1, "", uid)
                        }
                    }
                }
            }

        })
    }

    //todo 9
    private fun insertUser(name: String, email: String, hp: String, idUser: String?)
            : Boolean {
        val user = Users()
        user.email = email
        user.name = name
        user.hp = hp
        user.uid = auth?.uid

        val database = FirebaseDatabase.getInstance()
        val key = database.reference.push().key
        val myref = database.getReference(Constant.tb_user)

        myref.child(key?:"")
            .setValue(user)

        startActivity<AuthentificationHpActivity>(Constant.key to key)

        return true
    }
}