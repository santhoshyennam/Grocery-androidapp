package com.a2zdukhana.store

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_otp.terms
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class OtpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        var cotp = intent.getStringExtra(("otp"))!!
        button.setOnClickListener{
            var otpx  = otp.text.toString()
            val credential = PhoneAuthProvider.getCredential(cotp, otpx)
            signInWithCredential(credential)

        }
        terms.setOnClickListener {
            startActivity(Intent(this,Aboutus::class.java)
                .putExtra("title","privacy"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
        }


    }
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            Toast.makeText(this@OtpActivity, "Please connect to Internet", Toast.LENGTH_LONG).show()
        }
        else {
            var pd = ProgressDialog(this@OtpActivity)
            pd.setTitle("Verifying..")
            pd.show()
            var mAuth = FirebaseAuth.getInstance()
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {

                        /*val intent = Intent(this@SigninActivity, DetailsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)*/
                        pd.dismiss()
                        var uid = FirebaseAuth.getInstance().uid
                        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation")
                            .child(uid.toString())
                        dbase.addListenerForSingleValueEvent(
                            object : ValueEventListener
                            {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if(!p0.exists())
                                    {
                                        val formatter =
                                            SimpleDateFormat("dd.MM.yyyy, HH:mm")
                                        formatter.setLenient(false)

                                        val curDate = Date()
                                        val curTime: String = formatter.format(curDate)
                                        dbase.child("notificationtime").setValue(curTime)
                                        dbase.child("fullname").setValue(intent.getStringExtra("name"))
                                        dbase.child("mobile").setValue(intent.getStringExtra("email"))
                                        dbase.child("email").setValue(intent.getStringExtra("email"))

                                    }
                                }

                            }
                        )
                        var i = Intent(this@OtpActivity, MainActivity::class.java)
                        startActivity(i)
                        finish()

                    } else {
                        pd.dismiss()
                        Toast.makeText(this@OtpActivity, "invalid", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);


    }
}
