package com.a2zdukhana.store

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import kotlinx.android.synthetic.main.activity_order_placed.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class OrderPlaced : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        var id = intent.getStringExtra("orderid")
        var num = intent.getStringExtra("mobile")
        var mt = MyTask(this@OrderPlaced, id!!,num!!)
        mt.execute()
        orderid.text = id
        //Toast.makeText(this,num,Toast.LENGTH_LONG).show()
        done.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        moveTaskToBack(true)
    }
   class MyTask(var context:OrderPlaced,var x:String,var num:String
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
                           val message = "&message=" +("you have an order! \n orderid: "+x)
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

           var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
           StrictMode.setThreadPolicy(policy)


       }

       override fun onPostExecute(result: Unit?) {
           super.onPostExecute(result)

       }
   }
}