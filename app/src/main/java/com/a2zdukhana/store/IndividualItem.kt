package com.a2zdukhana.store

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.a2zdukhana.store.classes.CostClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_individual_item.*
import kotlinx.android.synthetic.main.activity_individual_item.add
import kotlinx.android.synthetic.main.activity_individual_item.back
import kotlinx.android.synthetic.main.activity_individual_item.cart
import kotlinx.android.synthetic.main.activity_individual_item.ccount
import kotlinx.android.synthetic.main.activity_individual_item.cost
import kotlinx.android.synthetic.main.activity_individual_item.image
import kotlinx.android.synthetic.main.activity_individual_item.minus
import kotlinx.android.synthetic.main.activity_individual_item.name
import kotlinx.android.synthetic.main.activity_individual_item.plus
import kotlinx.android.synthetic.main.activity_individual_item.plusmi

class IndividualItem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_item)
         var cat= intent.getStringExtra("category")
        var subcat= intent.getStringExtra("subcategory")
        var key = intent.getStringExtra("key")
        var quantity = intent.getStringExtra("quantity")
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }

        var dbase= FirebaseDatabase.getInstance().getReference("categories").child(cat!!).child("subcategory").child(subcat!!).child(key!!)
        dbase.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var imurl = ""
                    var c = mutableListOf<CostClass>()
                    chi.forEach {
                        when(it.key)
                        {
                            "quantity"->{
                                var chi2 = it.children
                                var map1 = HashMap<String,String>()
                                chi2.forEach {
                                    var chi3 = it.children
                                    map1.put("available","1")
                                    map1.put("key",it.key.toString())
                                    chi3.forEach {
                                        map1.put(it.key.toString(),it.value.toString())
                                    }
                                    var co = CostClass(map1.getValue("quantity"),map1.getValue("cost")
                                        ,map1.getValue("discount"),map1.getValue("key"),map1.getValue("available"))
                                    c.add(co)
                                }

                            }
                            "name"->{
                                name.text = it.value.toString()
                            }
                            "desc"->{
                                desc.text = it.value.toString()
                            }
                            "image"->{
                                imurl = it.value.toString()
                                Glide.with(this@IndividualItem).load(it.value.toString()).into(image)
                            }

                        }
                    }

                    var x= 0
                    var a = mutableListOf<String>()
                    while (x<c.size)
                    {
                        a.add(c[x].quantity)
                        x++
                    }
                    sp.adapter = ArrayAdapter(this@IndividualItem,R.layout.support_simple_spinner_dropdown_item,a)
                    sp.setSelection(a.indexOf(quantity))

                    var db1 = FirebaseDatabase.getInstance().getReference("usersinformation").child(FirebaseAuth.getInstance().uid.toString())
                        .child("cart").child(cat+":"+subcat+":"+key+":"+sp.selectedItemPosition)
                    db1.addListenerForSingleValueEvent(
                        object : ValueEventListener
                        {
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p2: DataSnapshot) {
                                if(p2.exists())
                                {
                                    cc.text = p2.value.toString()
                                    add.visibility = View.GONE
                                    plusmi.visibility = View.VISIBLE

                                }
                                else{
                                    add.visibility = View.VISIBLE
                                    plusmi.visibility = View.GONE
                                }

                            }


                        }
                    )
                    sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(p0: AdapterView<*>?) {

                        }

                        override fun onItemSelected(q0: AdapterView<*>?, q1: View?, q2: Int, q3: Long) {

                            cost.text = "₹"+String.format("%.2f",c[q2].cost.toFloat() - c[q2].discount.toFloat())
                            changeDetails(cat,subcat,key,c[q2].quantity)

                        }

                    }

                    add.setOnClickListener {

                        var user = FirebaseAuth.getInstance().currentUser
                        if(user==null)
                        {

                            var alert= AlertDialog.Builder(this@IndividualItem)
                            alert.setMessage("please login to add items to your cart")
                                .setCancelable(false)
                                .setPositiveButton("login",
                                    DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                                    startActivity(Intent(this@IndividualItem,LoginActivity::class.java))
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
                        else if(c[sp.selectedItemPosition].available.equals("0"))
                        {
                            //Toast.makeText(this@IndividualItem,""+sp.selectedItemPosition,Toast.LENGTH_LONG).show()
                            val builder =
                                AlertDialog.Builder(this@IndividualItem)
                            builder.setMessage("Out of Stock!")
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
                        else{
                            var uid = FirebaseAuth.getInstance().uid
                            var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                                .child(uid.toString()).child("cart").child(cat+":"+subcat+":"+key+":"+sp.selectedItemPosition)
                            db.setValue("1")
                            changeDetails(cat,subcat,key,sp.selectedItem.toString())
                        }


                    }

                    plus.setOnClickListener {
                        if(cc.text.toString().toInt()>9)
                        {
                            val builder =
                                AlertDialog.Builder(this@IndividualItem)
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
                        else if(c[sp.selectedItemPosition].available.equals("0"))
                        {
                            //Toast.makeText(this@IndividualItem,""+sp.selectedItemPosition,Toast.LENGTH_LONG).show()
                            val builder =
                                AlertDialog.Builder(this@IndividualItem)
                            builder.setMessage("Out of Stock!")
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
                        else {
                            var uid = FirebaseAuth.getInstance().uid
                            var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                                .child(uid.toString()).child("cart")
                                .child(cat + ":" + subcat + ":" + key + ":" + sp.selectedItemPosition)
                            db.addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        db.setValue((p0.value.toString().toInt() + 1).toString())
                                        changeDetails(cat, subcat, key, sp.selectedItem.toString())

                                    }

                                }
                            )
                        }
                    }

                    minus.setOnClickListener {

                        var uid = FirebaseAuth.getInstance().uid
                        var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                            .child(uid.toString()).child("cart").child(cat+":"+subcat+":"+key+":"+sp.selectedItemPosition)
                        db.addListenerForSingleValueEvent(
                            object : ValueEventListener
                            {
                                override fun onCancelled(p0: DatabaseError) {

                                }
                                override fun onDataChange(p0: DataSnapshot) {
                                    if(p0.value.toString().equals("1"))
                                        db.removeValue()
                                    else
                                        db.setValue((p0.value.toString().toInt()-1).toString())

                                    changeDetails(cat,subcat,key,sp.selectedItem.toString())

                                }
                            }
                        )

                    }

                }

            }
        )




        cart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java).putExtra("ac","other"))

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }

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

    private fun changeDetails(cat: String?, subcat: String?, key: String?, quantity: String) {

        var db1 = FirebaseDatabase.getInstance().getReference("usersinformation").child(FirebaseAuth.getInstance().uid.toString())
            .child("cart").child(cat+":"+subcat+":"+key+":"+sp.selectedItemPosition)
        db1.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p2: DataSnapshot) {
                    if(p2.exists())
                    {
                        cc.text = p2.value.toString()
                        add.visibility = View.GONE
                        plusmi.visibility = View.VISIBLE

                    }
                    else{
                        add.visibility = View.VISIBLE
                        plusmi.visibility = View.GONE
                    }

                }


            }
        )



    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);



    }
}