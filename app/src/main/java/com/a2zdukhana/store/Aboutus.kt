package com.a2zdukhana.store

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_aboutus.*
import kotlinx.android.synthetic.main.activity_aboutus.back

@Suppress("DEPRECATION")
class Aboutus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aboutus)
        var title = intent.getStringExtra("title")
        back.setOnClickListener {
            finish()

            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


        }

        if(title.equals("about"))
        {
            tv1.text = "About Us"
        }
        else if(title.equals("terms")){
            tv1.text = "Terms and Conditions"
        }
        else if(title.equals("privacy"))
        {
            tv1.text = "Privacy Policy"
        }
        var pDialog = ProgressDialog(this@Aboutus)
        pDialog.setMessage("Please wait page is loading....")
        pDialog.show()
        var db = FirebaseDatabase.getInstance().getReference("myinformation").child(title!!)
        db.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    wview.loadUrl(p0.value.toString())
                    pDialog.dismiss()

                }
            })
       /* wview.webViewClient = object: WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                pDialog.show()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                pDialog.dismiss()
            }
        }*/


        wview.settings.javaScriptEnabled = true
        wview.settings.builtInZoomControls = true
    }

    override fun onStart() {
        super.onStart()
        /*var user = FirebaseAuth.getInstance().currentUser
        if(user==null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }*/
        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            startActivity(Intent(this,NetConnection::class.java))
        }
    }
}