package com.a2zdukhana.store

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zdukhana.store.adapters.NotificationAdapter
import com.a2zdukhana.store.classes.NotificationClass
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_notification.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        var lm= LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL,false)
        rview.layoutManager = lm
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }
        var uid= FirebaseAuth.getInstance().uid
        clear.setOnClickListener {
            var alert= AlertDialog.Builder(this@NotificationActivity)
            alert.setMessage("Do you want to clear all Notifications?")
                .setCancelable(false)
                .setPositiveButton("yes",
                    DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                    var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("notificationtime")
                        val formatter =
                            SimpleDateFormat("dd.MM.yyyy, HH:mm")
                        formatter.setLenient(false)
                        val curDate = Date()
                        val curTime: String = formatter.format(curDate)
                        db.setValue(curTime)
                        rview.visibility = View.GONE
                        nonoti.visibility = View.VISIBLE
                        l1.visibility = View.GONE
                    })
                .setNegativeButton("no",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                })
            var al = alert.create()
            al.show()

        }
        var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("notificationtime")
        db.addValueEventListener(
            object :ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {
                    
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()) {
                        var x = p0.value.toString()
                        val formatter =
                            SimpleDateFormat("dd.MM.yyyy, HH:mm")
                        formatter.setLenient(false)
                        val cdate = formatter.parse(x)!!
                        val ctime = cdate.time
                        notify(ctime)
                    }
                    else{
                        clear.visibility = View.GONE
                    }
                }

            }
        )


    }

    private fun notify(x: Long) {
        var dbase = FirebaseDatabase.getInstance().getReference("notifications")
        dbase.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var chi = p0.children
                        var lis = mutableListOf<NotificationClass>()
                        chi.forEach {
                            var map = HashMap<String,String>()
                            var chi1=it.children
                            chi1.forEach {
                                map.put(it.key.toString(),it.value.toString())
                            }
                            var time=map.getValue("time")
                            val formatter =
                                SimpleDateFormat("dd.MM.yyyy, HH:mm")
                            formatter.setLenient(false)
                            val cdate = formatter.parse(map.getValue("time"))
                            val ctime = cdate.time
                            if(ctime>x)
                            {
                                var n = NotificationClass(map.getValue("title"),map.getValue("body"),map.getValue("img"),map.getValue("time")
                                    ,map.getValue("category"),map.getValue("subcategory"),map.getValue("itemkey"))
                                lis.add(n)
                            }


                        }
                        if(lis.size>0)
                        {
                            lis.reverse()
                            rview.adapter = NotificationAdapter(this@NotificationActivity,lis)
                            l1.visibility=View.VISIBLE
                            rview.visibility = View.VISIBLE
                            spin_kit.visibility=View.GONE
                        }
                        else
                        {
                            l1.visibility = View.GONE
                            spin_kit.visibility = View.GONE
                            rview.visibility = View.GONE
                            nonoti.visibility = View.VISIBLE
                        }
                    }


                }


            }
        )
    }

    override fun onStart() {
        super.onStart()

        var user  = FirebaseAuth.getInstance().currentUser
        if(user==null)
        {
            startActivity(Intent(this,Mobilelogin::class.java))
            finish()
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }


        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            startActivity(Intent(this,NetConnection::class.java))
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