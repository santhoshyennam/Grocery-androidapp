package com.a2zdukhana.store.adapters


import android.content.Intent
import android.graphics.Color

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.a2zdukhana.store.MyOrdersActivity
import com.a2zdukhana.store.OrderItems
import com.a2zdukhana.store.R
import com.a2zdukhana.store.classes.OrderClass

import kotlinx.android.synthetic.main.order_view.view.*


class OrderAdapter(var activity: MyOrdersActivity, var lis:MutableList<OrderClass>): RecyclerView.Adapter<OrderAdapter.Myholder>() {
    override fun onBindViewHolder(p0: Myholder, p1: Int) {

        p0.oid!!.text = lis[p1].orderid
        p0.status!!.text = lis[p1].status
        if(p0.status!!.text.equals("pending"))
            p0.status!!.setTextColor(Color.parseColor("#7ebe43"))
        else
            p0.status!!.setTextColor(Color.parseColor("#FE2F3c"))
        p0.date!!.text = lis[p1].date
        p0.view!!.setOnClickListener {
            activity.startActivity(Intent(activity,OrderItems::class.java).putExtra("oid",lis[p1].orderid)
                .putExtra("address",lis[p1].address))
            activity.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderAdapter.Myholder {
        var inf = LayoutInflater.from(activity)
        var v = inf.inflate(R.layout.order_view, p0, false)
        var myholder = OrderAdapter.Myholder(v)
        /*v.setOnClickListener{
            activity.startActivity(Intent(activity,OrderItems::class.java).putExtra("oid",lis[myholder.adapterPosition].orderid)
                .putExtra("address",lis[myholder.adapterPosition].address))

        }*/
        return myholder
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    class Myholder(v: View) : RecyclerView.ViewHolder(v) {

        var oid: TextView? = null
        var date:TextView? =null
        var status:TextView? =null
        var view:Button? =null
        init {
           oid = v.oid
            //amount = v.amount
            date = v.date
            status = v.status
            view = v.viewdetails
        }

    }
}
