package com.a2zdukhana.store

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.a2zdukhana.store.classes.CostClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_slider_items.*
import kotlinx.android.synthetic.main.activity_slider_items.back
import kotlinx.android.synthetic.main.activity_slider_items.cart
import kotlinx.android.synthetic.main.activity_slider_items.ccount

class SliderItems : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slider_items)
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }
        cart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java).putExtra("ac","other"))

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        var cat = intent.getStringExtra("category")
        var subcat = intent.getStringExtra("subcategory")
        var itemkey = intent.getStringExtra("itemkey")
        //Toast.makeText(this,cat+" "+subcat+" "+itemkey,Toast.LENGTH_LONG).show()
        var db = FirebaseDatabase.getInstance().getReference("categories")
            .child(cat!!).child("subcategory")
            .child(subcat!!).child(itemkey!!)
        db.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p1: DataSnapshot) {
                    var chi1 = p1.children
                    var map = HashMap<String, String>()

                    var qu = (itemkey.toInt()+1).toString()
                    chi1.forEach {
                        when (it.key) {
                            "quantity" -> {
                                var chi2 = it.children
                                var map1 = HashMap<String, String>()
                                var c = mutableListOf<CostClass>()

                                chi2.forEach {

                                    var chi3 = it.children
                                    map1.put("key",it.key.toString())
                                    map1.put("available","1")
                                    chi3.forEach {
                                        map1.put(it.key.toString(), it.value.toString())
                                    }
                                    var co = CostClass(
                                        map1.getValue("quantity"),
                                        map1.getValue("cost"),
                                        map1.getValue("discount"),map1.getValue("key"),map1.getValue("available")
                                    )
                                    c.add(co)
                                    //Toast.makeText(this@CartActivity,map.getValue("cost"),Toast.LENGTH_LONG).show()
                                }

                                displayDetails(
                                    c[0].quantity,
                                    c[0].cost,
                                    c[0].discount,
                                    cat!!,
                                    subcat!!,
                                    itemkey!!,
                                    0,c[0].available
                                )
                                cardv.setOnClickListener {
                                    startActivity(Intent(this@SliderItems,IndividualItem::class.java)
                                        .putExtra
                                            ("key",itemkey).putExtra("category",cat).putExtra("subcategory",subcat)
                                        .putExtra("quantity",c[0].quantity))
                                }
                                var x = 0
                                // var a = arrayOf<String>()
                                var a = mutableListOf<String>()
                                while (x < c.size) {
                                    a.add(c[x].quantity)
                                    x++
                                }
                                val builder = AlertDialog.Builder(this@SliderItems)
                                lquan!!.setOnClickListener {
                                    builder.setTitle("Select Quantity")
                                    builder.setItems(
                                        a.toTypedArray(),
                                        DialogInterface.OnClickListener { dialog, item -> // Do something with the selection
                                            displayDetails(
                                                c[item].quantity,
                                                c[item].cost,
                                                c[item].discount,
                                                cat!!,
                                                subcat!!,
                                                itemkey!!,
                                                item,c[item].available
                                            )
                                        })
                                    builder.show()


                                }


                            }
                            else -> {
                                map.put(it.key.toString(), it.value.toString())
                            }

                        }


                    }
                    name.text = map.getValue("name")
                    Glide.with(this@SliderItems).load(map.getValue("image")).into(image)


                }
            })
    }
    private fun displayDetails(quantity: String, cost1: String, discount: String,cat:String,subcat:String,key:String,item:Int,a:String) {
        quan.text = quantity
        ddiscount.text ="₹ "+discount+" OFF"
        mrp.text ="\u20B9"+cost1
        mrp.setPaintFlags(mrp.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        //p0.discount!!.text = "You save  \u20B9 "+lis[p1].cost[0].discount
        cost.text = "\u20B9"+(cost1.toFloat()-discount.toFloat())
        var uid = FirebaseAuth.getInstance().uid
        var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(FirebaseAuth.getInstance().uid.toString())
            .child("cart").child(cat+":"+subcat+":"+key+":"+item.toString())
        db.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p2: DataSnapshot) {
                    if(p2.exists())
                    {
                        add.visibility = View.GONE
                        plusmi.visibility = View.VISIBLE
                        count.text = p2.value.toString()


                            plus.setOnClickListener {
                                if(count.text.toString().toInt()>9)
                                {
                                    val builder =
                                        AlertDialog.Builder(this@SliderItems)
                                    builder.setMessage("You have exceeded limit!")
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            "OK"
                                        ) { dialog, id ->
                                            //do things
                                            dialog.cancel()
                                        }
                                    val alert = builder.create()
                                    alert.show()
                                }
                                else if(!a.equals("0"))
                                {
                                    var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                                        .child(uid.toString()).child("cart").child(cat+":"+subcat+":"+key+":"+item)
                                    db.setValue((count.text.toString().toInt()+1).toString())
                                }
                                else
                                    Toast.makeText(this@SliderItems,"Out of Stock",Toast.LENGTH_LONG).show()


                            }
                            minus.setOnClickListener {

                                    var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                                        .child(uid.toString()).child("cart").child(cat+":"+subcat+":"+key+":"+item)
                                    if(count.text.toString().equals("1"))
                                        db.removeValue()
                                    else
                                        db.setValue((count.text.toString().toInt()-1).toString())


                            }




                    }
                    else
                    {
                        add.visibility = View.VISIBLE
                        add.setOnClickListener {
                            var user = FirebaseAuth.getInstance().currentUser
                            if(user==null)
                            {

                                var alert= AlertDialog.Builder(this@SliderItems)
                                alert.setMessage("please login to add items to your cart")
                                    .setCancelable(false)
                                    .setPositiveButton("login",
                                        DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                                            startActivity(Intent(this@SliderItems,Mobilelogin::class.java))
                                            overridePendingTransition(R.anim.slide_in_right,
                                                R.anim.slide_out_left);

                                        })
                                    .setNegativeButton("cancel",
                                        DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                                            dialogInterface.cancel()
                                        })
                                var al = alert.create()
                                al.show()


                            }
                            else if (!a.equals("0")) {

                                var db =
                                    FirebaseDatabase.getInstance().getReference("usersinformation")
                                        .child(uid.toString()).child("cart")
                                        .child(cat + ":" + subcat + ":" + key + ":" + item)
                                db.setValue("1")

                            }
                            else
                                Toast.makeText(this@SliderItems,"Out of Stock",Toast.LENGTH_LONG).show()


                        }

                        plusmi.visibility = View.GONE

                    }
                }

            }
        )
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);

    }
}


