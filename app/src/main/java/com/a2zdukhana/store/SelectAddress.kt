package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zdukhana.store.adapters.LocationAdapter
import com.a2zdukhana.store.classes.LocationClass
import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_select_address.*

@Suppress("DEPRECATION")
class SelectAddress : AppCompatActivity(),AddAddress {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_address)
        var lm= LinearLayoutManager(this@SelectAddress, LinearLayoutManager.VERTICAL,false)
         rview.layoutManager = lm
        var uid = FirebaseAuth.getInstance().uid

        var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("mylocation")
        db.addValueEventListener(
             object : ValueEventListener
             {
                 override fun onCancelled(p0: DatabaseError) {

                 }

                 override fun onDataChange(p0: DataSnapshot) {
                     var chi = p0.children
                     var lis = mutableListOf<LocationClass>()
                     chi.forEach {
                         var k=it.key.toString()
                       var chi1 = it.children
                         var map = HashMap<String,String>()
                         chi1.forEach {
                             map.put(it.key.toString(),it.value.toString())
                         }
                         var l = LocationClass(map.getValue("locationname"),map.getValue("address"),map.getValue("area"),
                             map.getValue("landmark"),map.getValue("pincode"),map.getValue("lati"),map.getValue("longi"),map.getValue("key"),map.getValue("mobile"))
                         lis.add(l)
                         }
                    // Toast.makeText(this@SelectAddress,""+lis.size,Toast.LENGTH_LONG).show()
                     rview.adapter = LocationAdapter(this@SelectAddress,lis)
                     rview.visibility = View.VISIBLE
                     spin_kit.visibility = View.GONE
                 }

     })

        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


        }

}

    override fun addAddress(l: LocationClass) {
        var uid = FirebaseAuth.getInstance().uid
        var db =  FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("address")
        db.setValue(l)
        Toast.makeText(this,"default address is changed",Toast.LENGTH_LONG).show()
        finish()
    }

    override fun clearcart() {

    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
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