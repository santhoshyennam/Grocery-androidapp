package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

class FrontPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_page)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
         var h =Handler()
        h.postDelayed(
                object : Runnable{
                    override fun run() {
                        var sh = getSharedPreferences("startscreens",Context.MODE_PRIVATE)
                        var f =sh.getString("first",null)
                        if(f.equals(null))
                        {
                            var editor = sh.edit()
                            editor.putString("first","first")
                            editor.commit()
                            startActivity(Intent(this@FrontPage,StartScreens::class.java))
                            finish()
                        }
                        else
                        {

                                startActivity(Intent(this@FrontPage,MainActivity::class.java))
                                finish()

                        }
                    }

                },3000
        )

    }
}
