package com.a2zdukhana.store.adapters


import android.content.Context
import android.graphics.Paint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.a2zdukhana.store.R
import com.a2zdukhana.store.classes.CartClass
import kotlinx.android.synthetic.main.order_item.view.*


class OrderItemAdapter(var activity: Context, var lis:MutableList<CartClass>): RecyclerView.Adapter<OrderItemAdapter.Myholder>() {
    override fun onBindViewHolder(p0: Myholder, p1: Int) {
        p0.name!!.text = lis[p1].item.name
        p0.mrp!!.text ="₹ "+ String.format("%.2f",lis[p1].item.cost.toFloat()*lis[p1].count)
        p0.mrp!!.setPaintFlags(p0.mrp!!.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        p0.discount!!.text = "You save ₹"+ String.format("%.2f",lis[p1].item.discount.toFloat()*lis[p1].count)
        p0.cost!!.text = "₹"+ String.format("%.2f",(lis[p1].item.cost.toFloat()-lis[p1].item.discount.toFloat())*lis[p1].count)
        Glide.with(activity).load(lis[p1].item.image).into(p0.im!!)
        p0.count!!.text = "no.of items taken: "+lis[p1].count.toString()
        p0.quan!!.text = "Quantity: "+lis[p1].item.quantity


    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderItemAdapter.Myholder {
        var inf = LayoutInflater.from(activity)
        var v = inf.inflate(R.layout.order_item, p0, false)
        var myholder = OrderItemAdapter.Myholder(v)
        return myholder
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    class Myholder(v: View) : RecyclerView.ViewHolder(v) {

        var discount: TextView? = null
        var cost:TextView? =null
        var mrp:TextView? =null
        var im:ImageView? =null
        var name:TextView? =null
        var add:Button? =null
        var count:TextView? =null
        var quan:TextView? =null
        init {
            discount = v.discount
            cost = v.cost
            mrp = v.mrp
            name = v.name
            im = v.image
            count = v.count
            quan = v.quan

        }

    }
}
