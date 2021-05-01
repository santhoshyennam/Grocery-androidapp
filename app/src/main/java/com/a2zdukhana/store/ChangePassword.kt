package com.a2zdukhana.store

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_change_password.*

@Suppress("DEPRECATION")
class ChangePassword : AppCompatActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


        }
        change.setOnClickListener {
            var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var p = pass.text.toString()
            var c = cpass.text.toString()
            var n = npass.text.toString()
            if(p.isEmpty())
                pass.setError("Enter!")
            else if(c.isEmpty())
                cpass.setError("Enter!")
            else if(n.isEmpty())
                npass.setError("Enter!")
            else if( c.length<8)
                cpass.setError("less than 8 digits!")
            else if(!c.equals(n))
                Toast.makeText(this,"password not matched",Toast.LENGTH_LONG).show()
            else if(cm.activeNetworkInfo==null)
            {
                Toast.makeText(this@ChangePassword,"Please connect to internet",Toast.LENGTH_LONG).show()

            }
            else
            {
                var pd = ProgressDialog(this@ChangePassword)
                pd.setTitle("Changing password..")
                pd.show()
                var user = FirebaseAuth.getInstance().getCurrentUser();
                val  email = user!!.getEmail();
                var credential = EmailAuthProvider.getCredential(email!!,pass.text.toString());
                user.reauthenticate(credential).addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if (p0.isSuccessful) {

                                user.updatePassword(npass.text.toString()).addOnCompleteListener(
                                    object : OnCompleteListener<Void> {
                                        override fun onComplete(p1: Task<Void>) {
                                            if (!p1.isSuccessful) {
                                                pd.dismiss()
                                                Toast.makeText(this@ChangePassword,"something went wrong..try later",Toast.LENGTH_LONG).show()

                                            }
                                            else
                                            {
                                                pd.dismiss()
                                                Toast.makeText(this@ChangePassword,"successfully updated",Toast.LENGTH_LONG).show()
                                                finish()

                                            }
                                        }

                                    }
                                )


                        }
                        else
                        {
                            pd.dismiss()
                            Toast.makeText(this@ChangePassword,"wrong current password",Toast.LENGTH_LONG).show()
                        }
                    }
                })

            }
        }
    }
    override fun onStart() {
        super.onStart()
        var user  = FirebaseAuth.getInstance().currentUser
        if(user==null)
        {
            startActivity(Intent(this,LoginActivity::class.java))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);



    }
}