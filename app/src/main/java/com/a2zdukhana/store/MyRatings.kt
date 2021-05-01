package com.a2zdukhana.store

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_my_ratings.*

class MyRatings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ratings)

        back.setOnClickListener {
            finish()

            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }
        start.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);


    }
}