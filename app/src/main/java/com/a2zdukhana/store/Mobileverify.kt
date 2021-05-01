package com.a2zdukhana.store

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_mobileverify.*

class Mobileverify : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobileverify)
        var limit = 40
        var timer1=object : CountDownTimer((limit*1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //expire.setText("expires in "+java.lang.String.valueOf(x))
                timer.text = "00:"+limit.toString()
                limit--
            }

            override fun onFinish() {
                timer.setText("please login with mobile number instead")
            }
        }
        timer1.start()
        var r = intent.getStringExtra("random")
        var m = intent.getStringExtra("mobile")
        verify.setOnClickListener {
            if(otp.text.toString().isEmpty())
                Toast.makeText(this,"enter otp",Toast.LENGTH_LONG).show()
            else if(!r.equals(otp.text.toString()))
                Toast.makeText(this,"wrong otp",Toast.LENGTH_LONG).show()
            else
            {
                var uid = FirebaseAuth.getInstance().uid
                var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString())
                db.child("mobile").setValue(m!!)
                Toast.makeText(this,"updated your mobile number",Toast.LENGTH_LONG).show()
                finish()
            }

        }

        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);
    }
}