package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zdukhana.store.adapters.HorizantalAdapter
import com.a2zdukhana.store.classes.CartClass
import com.a2zdukhana.store.classes.ItemClass1
import com.a2zdukhana.store.fragments.ItemsFrag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_items.*


@Suppress("DEPRECATION")
class ItemsActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
         var hm= LinearLayoutManager(this@ItemsActivity, LinearLayoutManager.HORIZONTAL,false)
        hrview.layoutManager = hm
        //starts

        var uid = FirebaseAuth.getInstance().uid
        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("cart")
        dbase.addValueEventListener(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        bottoml.visibility = View.VISIBLE
                        var lis = mutableListOf<CartClass>()
                        var chi = p0.children
                        var last = p0.children.last().key.toString()
                        chi.forEach {

                            var x = it.key.toString().split(":")
                            var db = FirebaseDatabase.getInstance().getReference("categories")
                                .child(x[0]).child("subcategory")
                                .child(x[1]).child(x[2])
                            var qu = (x[3].toInt() + 1).toString()
                            db.addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p1: DataSnapshot) {
                                        var chi1 = p1.children
                                        var map = HashMap<String, String>()


                                        chi1.forEach {
                                            when (it.key) {
                                                "quantity" -> {
                                                    var chi2 = it.children
                                                    chi2.forEach {
                                                        when (it.key) {
                                                            qu -> {
                                                                var chi3 = it.children
                                                                chi3.forEach {
                                                                    map.put(
                                                                        it.key.toString(),
                                                                        it.value.toString()
                                                                    )
                                                                }
                                                                //Toast.makeText(this@CartActivity,map.getValue("cost"),Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                        //Toast.makeText(this@CartActivity,it.key.toString(),Toast.LENGTH_LONG).show()

                                                    }
                                                }
                                                else -> {
                                                    map.put(
                                                        it.key.toString(),
                                                        it.value.toString()
                                                    )
                                                }
                                            }

                                        }
                                        var i = ItemClass1(
                                            map.getValue("name"),
                                            map.getValue("image"),
                                            map.getValue("cost"),
                                            map.getValue("desc"),
                                            map.getValue("discount"),
                                            x[0],
                                            x[1],
                                            "",
                                            map.getValue("quantity"), it.key.toString()
                                        )
                                        var c = CartClass(x[2], i, it.value.toString().toInt())
                                        lis.add(c)
                                        if (lis.size != 0 && it.key.toString().equals(last)) {
                                            var price = 0.0
                                            var discount1 = 0.0
                                            var stotal = 0.0
                                            for (i in 0..lis.size - 1) {
                                                price =
                                                    price + ((lis[i].item.cost.toFloat() - lis[i].item.discount.toFloat()) * lis[i].count)
                                                discount1 += lis[i].item.discount.toFloat() * lis[i].count
                                                stotal += lis[i].item.cost.toFloat() * lis[i].count
                                                //price = price * 2
                                            }
                                            //Toast.makeText(this@ItemsActivity,String.format("%.2f", price),Toast.LENGTH_LONG).show()
                                            cost1.text =   "\u20B9 "+String.format("%.2f", price)
                                            items.text = lis.size.toString()+" items"
                                            next.setOnClickListener {
                                                startActivity(
                                                    Intent(
                                                        this@ItemsActivity,
                                                        CartActivity::class.java
                                                    ).putExtra("cato",intent.getStringExtra("cato"))
                                                        .putExtra("subcato",intent.getStringExtra("subcato")).putExtra("ac","item").putExtra("activity"
                                                            ,intent.getStringExtra("activity"))
                                                        .putExtra("pos",intent.getStringExtra("pos"))
                                                )
                                                finish()
                                                overridePendingTransition(R.anim.slide_in_right,
                                                    R.anim.slide_out_left);

                                            }

                                        }


                                    }

                                }
                            )

                        }

                    }
                    else
                    {
                        bottoml.visibility = View.GONE
                    }

                }
            }
        )

        //ends
        var cato = intent.getStringExtra("cato")
        var subcat = intent.getStringExtra("subcato")
        var ac = intent.getStringExtra("activity")
        if(!(ac!!.equals("home")))
        {
            viewbar.visibility = View.GONE
            var dbase = FirebaseDatabase.getInstance().getReference("categories").child(cato!!).child("subcategory")
            dbase.addListenerForSingleValueEvent(
                object : ValueEventListener
                {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var chi = p0.children
                        var lis = mutableListOf<String>()
                        chi.forEach {
                            lis.add(it.key.toString())
                        }
                        hm.scrollToPosition(intent.getStringExtra("pos")!!.toInt())
                        hrview.adapter = HorizantalAdapter(this@ItemsActivity,lis,cato,intent.getStringExtra("pos")!!)
                        hrview.visibility = View.VISIBLE
                        spin_kit.visibility = View.GONE

                    }

                }
            )
        }
        else
        {
            spin_kit.visibility = View.GONE
            //Toast.makeText(this@ItemsActivity,cato, Toast.LENGTH_LONG).show()
        }

        cart.setOnClickListener {
            startActivity(Intent(this@ItemsActivity,CartActivity::class.java).putExtra("cato",intent.getStringExtra("cato"))
                .putExtra("subcato",intent.getStringExtra("subcato")).putExtra("ac","item").putExtra("activity",ac)
                .putExtra("pos",intent.getStringExtra("pos")))
            finish()

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }

        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }

        var bundle = Bundle()
        bundle.putString("cato",cato)
        bundle.putString("subcat",subcat)
        bundle.putString("activity",ac)
        var im = ItemsFrag()
        im.arguments = bundle
        var  frag = supportFragmentManager
        var tx = frag.beginTransaction()
        tx.add(R.id.frag1,im)
        tx.commit()


        var user =FirebaseAuth.getInstance().currentUser
        if(user==null)
            ccount.text ="0"
        else
        {
            var uid = FirebaseAuth.getInstance().uid
            var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("cart")

            db.addValueEventListener(
                object : ValueEventListener
                {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists())
                        {
                            var c=0
                            var chi = p0.children
                            chi.forEach {
                                c++
                            }
                            ccount.text = c.toString()
                        }
                        else
                        {
                            ccount.setText("0")
                        }
                    }

                }
            )
        }

    }


    /*override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }*/

    /*override fun addtoCa(lis:ItemClass) {
        var uid = FirebaseAuth.getInstance().uid
        var db = FirebaseDatabase.getInstance().getReference("usersinformation")
            .child(uid.toString()).child("cart")
        var x =db.push().key.toString()
       var dbase = db.child(x)
        dbase.setValue(lis)

        /*ib.child("cost").setValue(lis.cost)
        ib.child("discount").setValue(lis.discount)
        ib.child("desc").setValue(lis.desc)
        ib.child("image").setValue(lis.image)
        ib.child("name").setValue(lis.name)*/
        //dbase.child("count").setValue("1")

    }*/

    override fun onStart() {
        super.onStart()
        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            startActivity(Intent(this,NetConnection::class.java))
        }

    }

    override fun onRestart() {
        super.onRestart()
       /* val i = Intent(this@ItemsActivity, ItemsActivity::class.java)
        finish()
        overridePendingTransition(0, 0)
        startActivity(i)
        overridePendingTransition(0, 0)*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);

    }
}