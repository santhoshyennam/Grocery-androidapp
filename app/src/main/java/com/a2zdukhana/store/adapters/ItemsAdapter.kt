package com.a2zdukhana.store.adapters


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.a2zdukhana.store.IndividualItem
import com.a2zdukhana.store.Mobilelogin
import com.a2zdukhana.store.R
import com.a2zdukhana.store.classes.ItemClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.items_view.view.*


class ItemsAdapter(
    var activity: Context, var lis:MutableList<ItemClass>
): RecyclerView.Adapter<ItemsAdapter.Myholder>() {
    var uid = FirebaseAuth.getInstance().uid
    var p =0
    var list = mutableListOf<Int>()
    val builder = AlertDialog.Builder(activity)
    override fun onBindViewHolder(p0: Myholder, p1: Int) {
        var c=0
        var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(FirebaseAuth.getInstance().uid.toString())
            .child("cart").child(lis[p1].category+":"+lis[p1].subcategory+":"+lis[p1].key+":"+list[p1])
        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p2: DataSnapshot) {
                    if(p2.exists())
                    {
                        p0.add!!.visibility = View.GONE
                        p0.pm!!.visibility = View.VISIBLE
                        p0.count!!.text = p2.value.toString()
                    }
                    else
                    {
                        p0.add!!.visibility = View.VISIBLE
                        p0.pm!!.visibility = View.GONE

                    }
                }

            }
        )
        p0.name!!.text = lis[p1].name
        p0.mrp!!.text ="\u20B9"+lis[p1].cost[list[p1]].cost
        if(lis[p1].cost[ list[p1]].quantity.length>5)
            p0.quan!!.text = lis[p1].cost[ list[p1]].quantity.substring(0,5)+".."
        else
            p0.quan!!.text = lis[p1].cost[ list[p1]].quantity
        /*if(lis[p1].cost[ list[p1]].discount.equals("0"))
            p0.ddis!!.visibility = View.GONE
        else*/
        p0.ddis!!.text ="₹ "+lis[p1].cost[ list[p1]].discount+" OFF"
        p0.mrp!!.setPaintFlags(p0.mrp!!.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        //p0.discount!!.text = "You save  \u20B9 "+lis[p1].cost[0].discount
        p0.cost!!.text = "\u20B9"+(lis[p1].cost[ list[p1]].cost.toFloat()-lis[p1].cost[ list[p1]].discount.toFloat())
        Glide.with(activity).load(lis[p1].image).placeholder(R.mipmap.ic_launcher).into(p0.im!!)
        var x= 0
       // var a = arrayOf<String>()
        var a = mutableListOf<String>()
        while (x<lis[p1].cost.size)
        {
            a.add(lis[p1].cost[x].quantity)
            x++
        }
        p0.lquan!!.setOnClickListener {
            builder.setTitle("Select Quantity")
            builder.setItems(
                a.toTypedArray(),
                DialogInterface.OnClickListener { dialog, item -> // Do something with the selection
                    list[p1] = item
                    notifyItemChanged(p1)
                })
            builder.show()
        }
        var dbase = FirebaseDatabase.getInstance().getReference("categories").child(lis[p1].category).child("subcategory").child(lis[p1].subcategory)
            .child(lis[p1].key).child("quantity").child(lis[p1].cost[list[p1]].qkey).child("available")

        dbase.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p5: DataSnapshot) {
                    if(p5.exists()&&p5.value.toString().equals("0"))
                    {
                        p0.outst!!.visibility = View.VISIBLE
                        p0.add!!.setOnClickListener {
                            val builder =
                                AlertDialog.Builder(activity)
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
                            //Toast.makeText(activity,"Out of Stock",Toast.LENGTH_LONG).show()
                        }
                        p0.plus!!.setOnClickListener {
                            val builder =
                                AlertDialog.Builder(activity)
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
                            //Toast.makeText(activity,"Out of Stock",Toast.LENGTH_LONG).show()
                        }

                    }
                    else
                    {
                        p0.outst!!.visibility = View.GONE
                        p0.add!!.setOnClickListener {
                            //Toast.makeText(activity,lis[p1].cost[p].qkey,Toast.LENGTH_LONG).show()
                            var user = FirebaseAuth.getInstance().currentUser
                            if(user==null)
                            {

                                var alert= AlertDialog.Builder(activity)
                                alert.setMessage("please login to add items to your cart")
                                    .setCancelable(false)
                                    .setPositiveButton("login",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                                        activity.startActivity(Intent(activity,Mobilelogin::class.java))




                                    })
                                    .setNegativeButton("cancel",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                                        dialogInterface.cancel()
                                    })
                                var al = alert.create()
                                al.show()


                            }
                            else {

                                //activity.addtoCa(lis[p1])
                                var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                                    .child(uid.toString()).
                                    child("cart").child(lis[p1].category+":"+lis[p1].subcategory+":"+lis[p1].key+":"+list[p1])
                                db.setValue("1")
                                notifyItemChanged(p1)
                            }
                        }

                        p0.plus!!.setOnClickListener {
                            //Toast.makeText(activity,"pressed",Toast.LENGTH_LONG).show()
                            if(p0.count!!.text.toString().toInt()>9)
                            {
                                val builder =
                                    AlertDialog.Builder(activity)
                                builder.setMessage("You have exceeded limit!")
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        "OK"
                                    ) { dialog, id ->
                                        //do things
                                        dialog.cancel()
                                    }
                                val alert = builder.create()
                                alert.show()                            }
                            else
                            {
                                var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                                    .child(uid.toString()).child("cart").child(lis[p1].category+":"+lis[p1].subcategory+":"+lis[p1].key+":"+list[p1])
                                db.setValue((p0.count!!.text.toString().toInt()+1).toString())
                                notifyItemChanged(p1)
                            }



                        }
                        p0.minus!!.setOnClickListener {
                            //Toast.makeText(activity,"pressed",Toast.LENGTH_LONG).show()
                            var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                                .child(uid.toString()).child("cart").child(lis[p1].category+":"+lis[p1].subcategory+":"+lis[p1].key+":"+list[p1])

                            if(p0.count!!.text.toString().equals("1"))
                                db.removeValue()
                            else
                                db.setValue((p0.count!!.text.toString().toInt()-1).toString())
                            notifyItemChanged(p1)
                        }
                    }
                }

            }
        )



        //p0.cardv!!.setOnClickListener{}



    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemsAdapter.Myholder {
        var inf = LayoutInflater.from(activity)
        var v = inf.inflate(R.layout.items_view, p0, false)
        var myholder = ItemsAdapter.Myholder(v)
        for (i in 0..lis.size-1)
            list.add(0)
        v.setOnClickListener {
            var uid= FirebaseAuth.getInstance().uid
            var db = FirebaseDatabase.getInstance().getReference("usersinformation")
                .child(uid.toString()).child("cart")
            var x = db.push().key.toString()
            lis[myholder.adapterPosition].cartkey = x
            var i = Intent(activity,IndividualItem::class.java)
            i.putExtra("key",lis[myholder.adapterPosition].key)
             i.putExtra("category",lis[myholder.adapterPosition].category)
            i.putExtra("subcategory",lis[myholder.adapterPosition].subcategory)
            /*val image = Pair.create<View, String>(myholder.im, "image")
            val title = Pair.create<View, String>(myholder.name, "title")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity)*/

            i.putExtra("quantity",lis[myholder.adapterPosition].cost[list[myholder.adapterPosition]].quantity)
            activity.startActivity(i)



        }
        return myholder
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    class Myholder(v: View) : RecyclerView.ViewHolder(v) {

        //var discount: TextView? = null
        var cost:TextView? =null
        var mrp:TextView? =null
        var im:ImageView? =null
        var name:TextView? =null
        var add:LinearLayout? =null
        var add1: TextView ? =null
        var add2:ImageView? = null
        var pm:LinearLayout? = null
        var minus: ImageView? =null
        var plus: ImageView? =null
        var count:TextView ? =null
        var ddis:TextView ? =null
        var quan:TextView? =null
        var lquan:LinearLayout?=null
        var cardv:CardView ? =null
        var outst:TextView? =null
        init {
           //discount = v.discount
            cost = v.cost
            mrp = v.mrp
            name = v.name
            im = v.image
            add = v.add
            add1 = v.add1
            add2 = v.add2
            pm = v.plusmi
            minus = v.minus
            plus = v.plus
            count = v.count
            ddis = v.ddiscount
            quan = v.quan
            lquan = v.lquan
            cardv = v.cardv
            outst = v.outst
        }

    }
    fun updateList(x:MutableList<ItemClass>)
    {
        lis =x;
        notifyDataSetChanged()
    }
}
