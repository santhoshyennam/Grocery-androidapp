package com.a2zdukhana.store

import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.a2zdukhana.store.classes.LocationClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.map_address.*


class AddAddressActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_address)
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }
        pincode.prompt="Select Pincode"
        if(intent.getStringExtra("from")!=null)
        {
            var lati = intent.getStringExtra("lati")
            var longi = intent.getStringExtra("longi")
            lname.setText(intent.getStringExtra("lname"), TextView.BufferType.EDITABLE)
            address.setText(intent.getStringExtra("hno"), TextView.BufferType.EDITABLE)
            landmark.setText(intent.getStringExtra("landmark"), TextView.BufferType.EDITABLE)
            area.setText(intent.getStringExtra("area"), TextView.BufferType.EDITABLE)
            mob.setText(intent.getStringExtra("mobile"), TextView.BufferType.EDITABLE)
            save.setOnClickListener {
                var uid = FirebaseAuth.getInstance().uid
                var dbase = FirebaseDatabase.getInstance().getReference("usersinformation")
                    .child(uid.toString()).child("mylocation")
                var x=intent.getStringExtra("from")
                var l=LocationClass(lname.text.toString(),address.text.toString(),area.text.toString(),landmark.text.toString()
                    ,pincode.selectedItem.toString(),lati!!.toString(),longi!!.toString(),x!!,mob.text.toString())
                dbase.child(x!!).setValue(l)
                FirebaseDatabase.getInstance().getReference("usersinformation") .child(uid.toString()).child("address").setValue(l)
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                finish()

            }

        }
        else {


            save.setOnClickListener {
                if (TextUtils.isEmpty(lname.text.toString()) || TextUtils.isEmpty(address.text.toString()) || TextUtils.isEmpty(
                        landmark.text.toString()
                    ) || TextUtils.isEmpty(area.text.toString())
                    || (pincode.selectedItem == null) || mob.text.toString().isEmpty()
                )
                    Toast.makeText(this, "Fill all details", Toast.LENGTH_LONG).show()
                else {
                    var lati = intent.getStringExtra("lati")
                    var longi = intent.getStringExtra("longi")
                    var uid = FirebaseAuth.getInstance().uid
                    var dbase = FirebaseDatabase.getInstance().getReference("usersinformation")
                        .child(uid.toString()).child("mylocation")
                    var x = dbase.push().key.toString()
                    var l = LocationClass(
                        lname.text.toString(),
                        address.text.toString(),
                        area.text.toString(),
                        landmark.text.toString()
                        ,
                        pincode.selectedItem.toString(),
                        lati!!.toString(),
                        longi!!.toString(),
                        x,
                        mob.text.toString()
                    )
                    dbase.child(x).setValue(l)
                    FirebaseDatabase.getInstance().getReference("usersinformation")
                        .child(uid.toString()).child("address").setValue(l)
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                    finish()

                }

            }
        }
        var db = FirebaseDatabase.getInstance().getReference("deliveryprice")
        db.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var a = mutableListOf<String>()
                    chi.forEach {
                        a.add(it.key.toString())
                    }
                    var adapter = ArrayAdapter(this@AddAddressActivity,android.R.layout.simple_spinner_dropdown_item,a)
                    pincode.adapter = adapter
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