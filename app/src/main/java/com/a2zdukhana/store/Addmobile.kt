package com.a2zdukhana.store

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.CountDownTimer
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_addmobile.*
import kotlinx.android.synthetic.main.home_frag.view.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random


@Suppress("DEPRECATION")
class Addmobile : AppCompatActivity() {
    var pd:ProgressDialog? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addmobile)
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        }
            send.setOnClickListener {
                var num = otp.text.toString()
                if (num.isEmpty()) {
                    Toast.makeText(this,"enter mobile number",Toast.LENGTH_LONG).show()
                } else {
                    var uid = FirebaseAuth.getInstance().uid
                    var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("mobile")
                    db.setValue(num).addOnCompleteListener(
                        object : OnCompleteListener<Void>
                        {
                            override fun onComplete(p0: Task<Void>) {
                                finish()
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                );
                            }

                        }
                    )

                    /*val min = 100000
                    val max = 999999
                    pd = ProgressDialog(this)
                    pd!!.setTitle("please wait..")
                    pd!!.show()
                    val random = Random.nextInt((max - min) + 1) + min
                    /*Toast.makeText(this,random.toString(),Toast.LENGTH_LONG).show()
                    var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                    StrictMode.setThreadPolicy(policy)
                    sendsms(num, random)*/
                    var mt = Addmobile.MyTask(this@Addmobile, random, num!!)
                    mt.execute()*/

                }
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);

    }

    class MyTask(var context:Addmobile,var random: Int,var num:String
    ) : AsyncTask<Unit, Unit, Unit>()

    {

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: Unit?) {
            try
            {

                // Construct data
                val apiKey = "apikey=" + "BdVSZXzB3fo-rXDP8AqyIGYdTfUYOU0JsVkhohEahG"
                val message = "&message=" +(""+random+" is your OTP for A2ZDukhana for 1 time use. Do not share this OTP with anyone. Enjoy Shopping!")
                val sender = "&sender=" + "TXTLCL"
                val numbers = "&numbers=" + num

                // Send data
                val conn=
                    URL("https://api.textlocal.in/send/?").openConnection() as HttpURLConnection
                val data = apiKey + numbers + message + sender
                conn.setDoOutput(true)
                conn.setRequestMethod("POST")
                conn.setRequestProperty("Content-Length", Integer.toString(data.length))
                conn.getOutputStream().write(data.toByteArray(charset("UTF-8")))
                val rd = BufferedReader(InputStreamReader(conn.getInputStream()))
                val stringBuffer = StringBuffer()
                var line: String? = null
                while (rd.readLine().also({ line = it }) != null) {
                    stringBuffer.append(line!!)
                }
                rd.close()
            } catch (e: Exception) {

            }
            context.startActivity(Intent(context,Mobileverify::class.java).putExtra("mobile",num)
                .putExtra("random",random.toString()))
            context. overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            );
            context.finish()
            var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)


        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

        }
    }
}