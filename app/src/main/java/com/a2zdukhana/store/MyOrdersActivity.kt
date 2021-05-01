package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zdukhana.store.adapters.OrderAdapter
import com.a2zdukhana.store.classes.OrderClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.my_orders.*
import kotlinx.android.synthetic.main.my_orders.back
import kotlinx.android.synthetic.main.my_orders.rview

@Suppress("DEPRECATION")
class MyOrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_orders)
        var lm= LinearLayoutManager(this@MyOrdersActivity, LinearLayoutManager.VERTICAL,false)
        rview.layoutManager = lm

        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


        }
        start.setOnClickListener {
            val c = Pair.create<View, String>(start, "dukhana")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyOrdersActivity,MainActivity::class.java), options.toBundle())
            }
            else
            startActivity(Intent(this,MainActivity::class.java))
        }

        var uid = FirebaseAuth.getInstance().uid
        var db= FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("orders")

        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        l1.visibility = View.GONE
                        var lis = mutableListOf<OrderClass>()
                        var chi  =p0.children
                        chi.forEach {
                            var map = HashMap<String,String>()
                            var orderid = it.key.toString()
                            map.put("orderid",orderid)
                            var chi1 = it.children
                            chi1.forEach {
                                when(it.key)
                                {
                                    "details"->{


                                    }
                                    else ->map.put(it.key.toString(),it.value.toString())
                                }
                            }
                            var o = OrderClass(map.getValue("orderid"),map.getValue("amount"),map.getValue("datetime"),map.getValue("type"),map.getValue("timeslot"),
                                map.getValue("address"),map.getValue("status"))
                            lis.add(o)
                        }
                        //Toast.makeText(this@MyOrdersActivity,""+lis[0].orderid,Toast.LENGTH_SHORT).show()
                        if(lis.size!=0) {
                            lis.reverse()
                            rview.adapter = OrderAdapter(this@MyOrdersActivity, lis)
                            spin_kit.visibility = View.GONE
                            rview.visibility = View.VISIBLE
                        }

                    }
                    else
                    {
                        rview.visibility = View.GONE
                        l1.visibility = View.VISIBLE
                        spin_kit.visibility = View.GONE
                    }
                    spin_kit.visibility =View.GONE
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