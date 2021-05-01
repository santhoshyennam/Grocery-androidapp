package com.a2zdukhana.store

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient

    var RC_SIGN_IN=2
    private lateinit var auth: FirebaseAuth
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        create.setOnClickListener {
            var i = Intent(this@LoginActivity, RegisterActivity::class.java)
            val email = Pair.create<View, String>(email, "email")
            val pass = Pair.create<View, String>(password, "otp")
            val button = Pair.create<View, String>(login, "button")
            val c = Pair.create<View, String>(tit, "title")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity, email,pass,button,c)
            startActivity(i, options.toBundle())

        }
        terms.setOnClickListener {
            startActivity(Intent(this,Aboutus::class.java)
                .putExtra("title","privacy"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
        }


        forgot.setOnClickListener {
            var i = Intent(this@LoginActivity, ForgotActivity::class.java)
            val forgot = Pair.create<View, String>(forgot, "email")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity, forgot)

            startActivity(i,options.toBundle())


        }

        login.setOnClickListener {
            var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var e=email.text.toString()
            var p = password.text.toString()
            if(e.isEmpty())
                email.setError("Email!")
            else if(p.isEmpty())
                password.setError("Password!")
            else if(p.length<8)
                password.setError("less than 8 digits")
            else if(cm.activeNetworkInfo==null)
            {
                Toast.makeText(this@LoginActivity,"Please connect to internet",Toast.LENGTH_LONG).show()

            }
            else
            {
                var pd = ProgressDialog(this@LoginActivity)
                pd.setTitle("Authenticating..")
                pd.show()
                var auth = FirebaseAuth.getInstance()

                auth.signInWithEmailAndPassword(e,p).addOnCompleteListener{
                    if(it.isSuccessful)
                    {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                        pd.dismiss()
                    }
                    else
                    {
                        Toast.makeText(this@LoginActivity,"invalid email or password",Toast.LENGTH_LONG).show()
                        pd.dismiss()
                    }
                }
            }
        }


    }


}
