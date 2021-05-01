package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.a2zdukhana.store.adapters.TimeslotAdapter
import android.app.ProgressDialog
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_select_delivery.*
import kotlinx.android.synthetic.main.activity_select_delivery.back
import kotlinx.android.synthetic.main.activity_select_delivery.spin_kit
import kotlinx.android.synthetic.main.freedelivery_view.view.*


@Suppress("DEPRECATION")
class SelectDelivery : AppCompatActivity() {
    var myad:String ? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_delivery)
        var lm= GridLayoutManager(this@SelectDelivery,2)
        rview.layoutManager = lm
        loadUserDetails()
        var prev = 0
        subtotal.text  = "₹"+intent.getStringExtra("subtotal")
        discount.text = "₹"+intent.getStringExtra("discount")

        //Toast.makeText(this,intent.getStringExtra("price"),Toast.LENGTH_LONG).show()
        change.setOnClickListener {
            var i = Intent(this@SelectDelivery,MyAddressActivity::class.java)
            startActivity(i)
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);



        }
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }



    }

    private fun loadUserDetails() {
        var uid = FirebaseAuth.getInstance().uid
        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("address")

        dbase.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var chi = p0.children
                        var map = HashMap<String,String>()
                        chi.forEach {
                                    map.put(it.key.toString(),it.value.toString())
                        }

                        myaddress.text = "Location Name:"+map.getValue("locationname")+"\nAddress: "+map.getValue("address")+"\n"+"Area: "+map.getValue("area")+"\n"+
                                "Landmark: "+map.getValue("landmark")+"\n"+
                                "Pincode: "+map.getValue("pincode")

                        var db = FirebaseDatabase.getInstance().getReference("deliveryprice").child(map.getValue("pincode"))
                        db.addValueEventListener(
                            object : ValueEventListener
                            {
                                override fun onCancelled(p0: DatabaseError) {

                                }

                                override fun onDataChange(p0: DataSnapshot) {
                                    if(p0.exists())
                                    {
                                        var x = intent.getStringExtra("subtotal")!!.toFloat()
                                        var y = intent.getStringExtra("discount")!!.toFloat()
                                        var z = p0.value.toString().toFloat()
                                        delivery.text = "₹"+p0.value.toString()
                                        total.text = "₹"+ String.format("%.2f",x-y+z)
                                    }
                                    else
                                    {
                                        total.text = ""
                                        delivery.text = "---"
                                        Toast.makeText(this@SelectDelivery,"We cant delivery to this Pincode",Toast.LENGTH_LONG).show()
                                    }
                                }

                            }
                        )

                    }
                    else{
                        change.setText("ADD")
                    }
                    real.visibility = View.VISIBLE
                    spin_kit.visibility = View.GONE

                }

            })
        var db= FirebaseDatabase.getInstance().getReference("timeslot")
        db.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var lis = mutableListOf<String>()
                    chi.forEach {
                        lis.add(it.value.toString())
                    }
                    var adapter = TimeslotAdapter(this@SelectDelivery,lis)
                    rview.adapter = adapter

                    pay.setOnClickListener {
                        paynow(adapter.getSelected())
                    }


                }

            }
        )

    }

    private fun paynow(selected: String) {

        //var price = intent.getStringExtra("price")
        var r = selected

        var timeslot = r
        var ad = myaddress.text
        if(r.equals("gone") )
            Toast.makeText(this,"Please select timeslot",Toast.LENGTH_LONG).show()
        else if(myaddress.text.toString().isEmpty()){
            Toast.makeText(this,"Please add Your address",Toast.LENGTH_LONG).show()
        }
        else {

            var uid = FirebaseAuth.getInstance().uid
            var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("mobile")
            db.addListenerForSingleValueEvent(
                object : ValueEventListener
                {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()&& !total.text.toString().equals(""))
                        {
                            alertMessage(timeslot)

                        }
                        else if(total.text.toString().equals(""))
                        {
                            Toast.makeText(this@SelectDelivery,"We cant delivery to this Pincode",Toast.LENGTH_LONG).show()
                        }
                        else
                        {
                            var dbase = FirebaseDatabase.getInstance().getReference("mobileverification")
                            dbase.addListenerForSingleValueEvent(
                                object : ValueEventListener
                                {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        if (p0.exists() && p0.value.toString().equals("0")) {
                                            alertMessage(timeslot)
                                        }else {

                                            var i =
                                                Intent(this@SelectDelivery, Addmobile::class.java)
                                            startActivity(i)
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            );


                                        }

                                    }

                                }
                            )




                        }
                    }

                }
            )

        }



    }


    fun alertMessage(timeslot:String)
    {
        var pd= ProgressDialog(this@SelectDelivery)
        pd.setTitle("Please wait..")
        pd.show()
        var alert = AlertDialog.Builder(this@SelectDelivery)
        var v1 = layoutInflater.inflate(
            R.layout.freedelivery_view,
            null
        )
        alert.setView(v1)


        var dbase = FirebaseDatabase.getInstance().getReference("freedeliverypopup")
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
                            "1"->v1.tv1.text=it.value.toString()
                            "2"->v1.tv2.text = it.value.toString()
                        }
                    }

                    pd.dismiss()
                    var alert1 = alert.create()
                    alert1.setCanceledOnTouchOutside(false)
                    alert1.show()

                }

            }
        )
        v1.next.setOnClickListener {
            var i = Intent(
                this@SelectDelivery,
                BillingActivity::class.java
            )
            i.putExtra(
                "price",
                total.text.toString().substring(1)
            )
            i.putExtra("timeslot", timeslot)
            i.putExtra("address", myaddress.text.toString())
            i.putExtra(
                "deliverycharges",
                delivery.text.toString()
            )
            startActivity(i)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            );
        }
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