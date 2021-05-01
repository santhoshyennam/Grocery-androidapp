package com.a2zdukhana.store.adapters


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.a2zdukhana.store.*
import com.a2zdukhana.store.classes.LocationClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.location_view.view.*


class LocationAdapter(var activity: SelectAddress, var lis:MutableList<LocationClass>): RecyclerView.Adapter<LocationAdapter.Myholder>() {

    var c= 0
    override fun onBindViewHolder(p0: Myholder, p1: Int) {

           if(c==p0.adapterPosition)
           {
               p0.check!!.isChecked = true
               p0.d!!.visibility = View.VISIBLE
           }
            else{
               p0.check!!.isChecked = false
               p0.d!!.visibility = View.GONE
           }

        p0.lname!!.text = lis[p1].locationname
        p0.address!!.text = "Address: "+lis[p1].address+"\n"+"Area: "+lis[p1].area+"\n"+"Landmark: "+lis[p1].landmark+"\n"+"Pincode: "+lis[p1].pincode+"\n mobile: "+lis[p1].mobile
        if(lis[p1].lati.equals("0"))
            p0.show!!.visibility = View.GONE
        p0.show!!.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr="+lis[p1].lati+","+lis[p1].longi)
            )
            activity.startActivity(intent)
            /*if(lis[p1].lati.equals("0"))
            {
                Toast.makeText(activity,"this address didnt contain GPS",Toast.LENGTH_LONG).show()
            }
            else {
                var i = Intent(activity, MapsActivity::class.java)
                i.putExtra("lati", lis[p1].lati)
                i.putExtra("longi", lis[p1].longi)
                i.putExtra("activity","contact")
                activity.startActivity(i)
            }*/
        }

        p0.d!!.setOnClickListener {
            activity.addAddress(lis[p1])
        }
        p0.delete!!.setOnClickListener {
            var uid = FirebaseAuth.getInstance().uid
            var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("mylocation")

            var alert= AlertDialog.Builder(activity)
            alert.setMessage("Do you want to delete?")
                .setCancelable(false)
                .setPositiveButton("yes",
                    DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                        db.child(lis[p1].key).removeValue()
                })
                .setNegativeButton("no",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                })
            var al = alert.create()
            al.show()

        }
        p0.edit!!.setOnClickListener {
            activity.startActivity(Intent(activity,AddAddressActivity::class.java)
                .putExtra("lname",lis[p1].locationname).putExtra("hno",lis[p1].address)
                .putExtra("landmark",lis[p1].landmark).putExtra("area",lis[p1].area).putExtra("mobile",lis[p1].mobile)
                .putExtra("pincode",lis[p1].pincode).putExtra("from",lis[p1].key)
                .putExtra("lati",lis[p1].lati).putExtra("longi",lis[p1].longi))
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LocationAdapter.Myholder {
        var inf = LayoutInflater.from(activity)
        var v = inf.inflate(R.layout.location_view, p0, false)
        var myholder = LocationAdapter.Myholder(v)

        v.setOnClickListener {
            myholder.check!!.isChecked = true
            myholder.d!!.visibility = View.VISIBLE
            if(c!=myholder.adapterPosition)
            {
                notifyItemChanged(c)
                c=myholder.adapterPosition
            }


        }
        return myholder
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    class Myholder(v: View) : RecyclerView.ViewHolder(v) {

        var lname: TextView? = null
        var address:TextView? =null
        var show:LinearLayout? =null
        var delete:ImageView? =null
        var edit:ImageView? =null
        var check : RadioButton? =null
        var d: Button? =null
        init {
           lname = v.lname
            address = v.address
            show = v.show
            delete = v.delete
            check = v.check
            d = v.defaultadd
            edit = v.edit
        }

    }
}
