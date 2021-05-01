@file:Suppress("DEPRECATION")

package com.a2zdukhana.store

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.core.util.Pair
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.email
import kotlinx.android.synthetic.main.activity_register.password
import kotlinx.android.synthetic.main.activity_register.terms
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {
    lateinit var verificationid:String
    private lateinit var googleSignInClient: GoogleSignInClient

    var RC_SIGN_IN=12
    private lateinit var auth: FirebaseAuth

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        terms.setOnClickListener {
            startActivity(Intent(this,Aboutus::class.java)
                .putExtra("title","privacy"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
        }

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        sign.setOnClickListener {
            //val email = Pair<View, String>(name, "otp")

            // val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@RegisterActivity, email)
            var e = email.text.toString()
            var p = password.text.toString()
            var n = name.text.toString()
            var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(n.isEmpty())
                name.setError("name!")
            else if(n.length>30)
                name.setError("name should be less than 30 characters!")
            else if (e.isEmpty())
                email.setError("Email!")
            else if (p.isEmpty())
                password.setError("Password!")
            else if (p.length < 8)
                password.setError("less than 8 digits")
            else if(!p.equals(cpass.text.toString()))
            {
                Toast.makeText(this@RegisterActivity,"password not matched",Toast.LENGTH_LONG).show()
            }
            else if(cm.activeNetworkInfo==null)
            {
                Toast.makeText(this@RegisterActivity,"Please connect to internet",Toast.LENGTH_LONG).show()

            }
            else {
                var auth = FirebaseAuth.getInstance()

                var pd = ProgressDialog(this@RegisterActivity)
                pd.setTitle("Creating Account..")
                pd.show()
                auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener {
                    if (it.isSuccessful) {
                        pd.dismiss()
                        val formatter =
                            SimpleDateFormat("dd.MM.yyyy, HH:mm")
                        formatter.setLenient(false)

                        val curDate = Date()
                        val curTime: String = formatter.format(curDate)

                        var uid = FirebaseAuth.getInstance().uid
                        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString())
                        dbase.child("fullname").setValue(n)
                        dbase.child("email").setValue(e)
                        dbase.child("notificationtime").setValue(curTime)
                        startActivity(Intent(this@RegisterActivity,MainActivity::class.java))
                        finish()


                    } else {
                        pd.dismiss()
                        Toast.makeText(this@RegisterActivity, "Used by other user", Toast.LENGTH_LONG)
                            .show()
                    }


                }
            }


        }
        /*google.setOnClickListener {
            signIn()
        }*/
        already.setOnClickListener {
            var i = Intent(this@RegisterActivity, LoginActivity::class.java)
            val email = Pair.create<View, String>(email, "email")
            val pass = Pair.create<View, String>(password, "otp")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@RegisterActivity, email,pass)
            startActivity(i, options.toBundle())

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

    }

}
