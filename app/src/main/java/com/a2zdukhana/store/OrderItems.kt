package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zdukhana.store.adapters.OrderItemAdapter
import com.a2zdukhana.store.classes.CartClass
import com.a2zdukhana.store.classes.ItemClass1
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_order_items.*

@Suppress("DEPRECATION")
class OrderItems : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_items)
        var lm= LinearLayoutManager(this@OrderItems, LinearLayoutManager.VERTICAL,false)
        rview.layoutManager = lm
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }
        //address.text = intent.getStringExtra("address")
        oid.text = intent.getStringExtra("oid")!!
        var uid = FirebaseAuth.getInstance().uid

        var dba = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("orders")
            .child(intent.getStringExtra("oid")!!).child("deliverytime")
        dba.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(!p0.exists())
                    {
                        ldeliver.visibility = View.GONE
                        ldv.visibility = View.GONE
                    }
                }

            }
        )
        var db= FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("orders")
            .child(intent.getStringExtra("oid")!!).child("details")
        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("orders")
            .child(intent.getStringExtra("oid")!!)
        dbase.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    chi.forEach {
                        when(it.key)
                        {
                            "amount"->amount.text = it.value.toString()
                            "timeslot"->timeslot.text = it.value.toString()
                            "type"->payment.text = it.value.toString()
                            "address"->address.text = it.value.toString()
                            "status"->status.text = it.value.toString()
                            "datetime"-> {
                                otime.text = it.value.toString()
                                odate.text = it.value.toString()
                            }
                            "deliverytime"-> {
                                dtime.text = it.value.toString()
                            }
                            "deliverycharges"->dcharges.text=""+it.value.toString()

                        }
                    }
                }

            }
        )

        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var lis = mutableListOf<CartClass>()
                    chi.forEach {
                        var chi1 = it.children
                        var map = HashMap<String,String>()
                        map.put("key",it.key.toString())
                        chi1.forEach {
                            map.put(it.key.toString(),it.value.toString())
                            /*when(it.key)
                            {
                                "category"->{
                                    map.put("category",it.value.toString())
                                }
                                "subcategory"->{
                                    map.put("subcategory",it.value.toString())

                                }
                                "itemdetails"->{
                                    var chi2 = it.children
                                    chi2.forEach {
                                        map.put(it.key.toString(),it.value.toString())
                                    }
                                }
                                "count"->map.put(it.key.toString(),it.value.toString())
                            }*/
                        }
                        var i = ItemClass1(map.getValue("name"),map.getValue("image"),map.getValue("cost"),map.getValue("desc"),
                            map.getValue("discount"),map.getValue("category"),map.getValue("subcategory"),map.getValue("count"),map.getValue("quantity")
                        )
                        var c = CartClass(map.getValue("key"),i,map.getValue("count").toInt())
                        lis.add(c)
                    }
                    oitems.text = "Order Items ("+lis.size+")"
                    rview.adapter = OrderItemAdapter(this@OrderItems,lis)
                }

            }
        )


    }
    override fun onStart() {
        super.onStart()

        var user  = FirebaseAuth.getInstance().currentUser
        if(user==null)
        {
            startActivity(Intent(this,LoginActivity::class.java))


        }

        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            startActivity(Intent(this,NetConnection::class.java))

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);

    }
}