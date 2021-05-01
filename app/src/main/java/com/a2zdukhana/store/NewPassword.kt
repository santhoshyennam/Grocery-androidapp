package com.a2zdukhana.store

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_new_password.change
import kotlinx.android.synthetic.main.activity_new_password.cpass
import kotlinx.android.synthetic.main.activity_new_password.npass

@Suppress("DEPRECATION")
class NewPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        change.setOnClickListener {
            var n = npass.text.toString()
            var c = cpass.text.toString()
            var email = intent.getStringExtra("num")
            if(n.isEmpty())
                npass.setError("provide!")
            else if(c.isEmpty())
                cpass.setError("provide!")
            else if(n.length<8)
                npass.setError("less than 8 digits!")
            else if(!n.equals(c))
                Toast.makeText(this,"password not matched",Toast.LENGTH_LONG).show()
            else
            {
                var pd = ProgressDialog(this@NewPassword)
                pd.setTitle("Changing password..")
                pd.show()



            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);


    }
}