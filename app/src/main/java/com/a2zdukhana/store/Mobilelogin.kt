package com.a2zdukhana.store

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_mobilelogin.*
import kotlinx.android.synthetic.main.activity_mobilelogin.terms
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class Mobilelogin : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var verificationid:String
    var RC_SIGN_IN=2
    var pd : ProgressDialog? =null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobilelogin)
        createuser()
        google.setOnClickListener {
            signin()
        }

        terms.setOnClickListener {
            startActivity(Intent(this,Aboutus::class.java)
                .putExtra("title","privacy"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
        }


        lemail.setOnClickListener {
            var i = Intent(this@Mobilelogin, LoginActivity::class.java)
            val forgot = Pair.create<View, String>(mobile, "email")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@Mobilelogin, forgot)

            startActivity(i,options.toBundle())
        }

        send.setOnClickListener {
            var e = mobile.text.toString()
            if(name.text.toString().isEmpty())
                name.setError("enter your name")
            else if(e.isEmpty())
                mobile.setError("enter mobile")
            else if(e.length<10 || e.length>13)
                mobile.setError("enter valid number")
            else if (!e.startsWith("+91")) {
                e = "+91" + e
                pd = ProgressDialog(this)
                pd!!.setTitle("Please wait..")
                pd!!.show()
                sendVerificationCode(e)
            }
            else {
                pd = ProgressDialog(this)
                pd!!.setTitle("Please wait..")
                pd!!.show()
                sendVerificationCode(e)
            }
        }

    }


    private fun sendVerificationCode(number: String) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallBack
        )
    }

    private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(s, forceResendingToken)
            verificationid = s!!
            var i = Intent(this@Mobilelogin, OtpActivity::class.java)
            i.putExtra("otp", verificationid)
            i.putExtra("email",mobile.text.toString())
            i.putExtra("name",name.text.toString())
            val email = Pair.create<View, String>(send, "otp")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@Mobilelogin, email)
            startActivity(i, options.toBundle())

            pd!!.dismiss()

        }

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            //val code = phoneAuthCredential.smsCode

        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@Mobilelogin,"failed to send message", Toast.LENGTH_LONG).show()
            pd!!.dismiss()

        }
    }

    private fun signin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this@Mobilelogin,"failed to login", Toast.LENGTH_LONG).show()

            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //val user = auth.currentUser
                    var auth = FirebaseAuth.getInstance().uid
                    var f= FirebaseDatabase.getInstance().getReference("usersinformation").child(auth.toString())
                    var signin= GoogleSignIn.getLastSignedInAccount(this)
                    f.addListenerForSingleValueEvent(
                        object : ValueEventListener
                        {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(!p0.exists())
                                {
                                    f.child("fullname").setValue(signin!!.displayName.toString())
                                    f.child("email").setValue(signin.email)
                                    val formatter =
                                        SimpleDateFormat("dd.MM.yyyy, HH:mm")
                                    formatter.setLenient(false)

                                    val curDate = Date()
                                    val curTime: String = formatter.format(curDate)
                                    f.child("notificationtime").setValue(curTime)


                                }
                            }

                        }
                    )


                    //Toast.makeText(this@LoginActivity,"success",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@Mobilelogin,MainActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(this@Mobilelogin,"failed to login", Toast.LENGTH_LONG).show()
                }

                // ...
            }
    }
    fun createuser()
    {
        var gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // [END initialize_auth]
    }
    /*override fun onStart() {
        super.onStart()

        var user  = FirebaseAuth.getInstance().currentUser
        if(user!=null)
        {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }*/

    override fun onBackPressed() {
        super.onBackPressed()

    }
}