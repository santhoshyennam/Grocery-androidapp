package com.a2zdukhana.store.adapters


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.a2zdukhana.store.*
import com.a2zdukhana.store.classes.NotificationClass
import kotlinx.android.synthetic.main.noti_view.view.*


class NotificationAdapter(var activity: NotificationActivity, var lis:MutableList<NotificationClass>): RecyclerView.Adapter<NotificationAdapter.Myholder>() {


    override fun onBindViewHolder(p0: Myholder, p1: Int) {
       p0.title!!.text = lis[p1].title
        p0.body!!.text = lis[p1].body
        p0.date!!.text = "Notification on "+lis[p1].time
        Glide.with(activity).load(lis[p1].image).into(p0.img!!)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotificationAdapter.Myholder {
        var inf = LayoutInflater.from(activity)
        var v = inf.inflate(R.layout.noti_view, p0, false)
        var myholder = NotificationAdapter.Myholder(v)

        v.setOnClickListener {
            activity.startActivity(Intent(activity,SliderItems::class.java)
                .putExtra("category",lis[myholder.adapterPosition].category)
                .putExtra("subcategory",lis[myholder.adapterPosition].subcategory)
                .putExtra("itemkey",lis[myholder.adapterPosition].itemkey))
            activity.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }

        return myholder
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    class Myholder(v: View) : RecyclerView.ViewHolder(v) {

        var title: TextView? = null
        var body: TextView? = null
        var img: ImageView? = null
        var date: TextView? = null

        init {
            title = v.title
            body = v.body
            img = v.img
            date = v.date
        }

    }
}
