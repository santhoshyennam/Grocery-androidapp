package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_contactus.*
import kotlinx.android.synthetic.main.activity_my_ratings.back


@Suppress("DEPRECATION")
class Contactus : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactus)

        back.setOnClickListener {
            finish()

            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


        }

        about.setOnClickListener {
            startActivity(Intent(this,Aboutus::class.java)
                .putExtra("title","about"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        terms.setOnClickListener {
            startActivity(Intent(this,Aboutus::class.java)
                .putExtra("title","terms"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        privacy.setOnClickListener {
            startActivity(Intent(this,Aboutus::class.java)
                .putExtra("title","privacy"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }

        var db = FirebaseDatabase.getInstance().getReference("myinformation")
        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var lati = ""
                    var longi = ""
                    chi.forEach {
                        when(it.key)
                        {
                            "whatsapp"->twh.text = it.value.toString()
                            "phone1"->mob1.text = it.value.toString()
                            "phone2"->mob2.text = it.value.toString()
                            "email"->temail.text = it.value.toString()
                            "address"->taddress.text = it.value.toString()
                            "latitude"->lati = it.value.toString()
                            "longitude"->longi =it.value.toString()

                        }
                    }
                    address.setOnClickListener {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="+lati+","+longi)
                        )
                        startActivity(intent)
                        /*startActivity(Intent(this@Contactus,MapsActivity::class.java).putExtra("lati",lati).putExtra("longi",longi)
                            .putExtra("activity","contact"))
                        overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);*/



                    }
                    mob1.setOnClickListener {
                        var i = Intent(Intent.ACTION_DIAL)
                        i.data= Uri.parse("tel:"+mob1.text.toString())
                        startActivity(i)


                    }
                    mob2.setOnClickListener {
                        var i = Intent(Intent.ACTION_DIAL)
                        i.data=Uri.parse("tel:"+mob2.text.toString())
                        startActivity(i)
                    }

                    whatsapp.setOnClickListener {
                        val phoneNumber = twh.text.toString()
                        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                        try {
                            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        } catch (e: PackageManager.NameNotFoundException) {
                            Toast.makeText(this@Contactus, "Whatsapp is not installed in your phone.", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }

                    email.setOnClickListener {

                        val email = Intent(Intent.ACTION_SEND)
                        email.putExtra(Intent.EXTRA_EMAIL, arrayOf(temail.text.toString()))
                        email.putExtra(Intent.EXTRA_SUBJECT, "")
                        email.putExtra(Intent.EXTRA_TEXT, "")

                        //need this to prompts email client only

                        //need this to prompts email client only
                        email.type = "message/rfc822"

                        startActivity(Intent.createChooser(email, "Choose an Email client :"))
                    }


                }

            }
        )
    }




    override fun onStart() {
        super.onStart()
        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            startActivity(Intent(this,NetConnection::class.java))
        }



    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);


    }



}