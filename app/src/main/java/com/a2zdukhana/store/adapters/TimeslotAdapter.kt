package com.a2zdukhana.store.adapters


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a2zdukhana.store.R
import kotlinx.android.synthetic.main.timeslot_view.view.*


class TimeslotAdapter(var activity: Context, var lis:MutableList<String>): RecyclerView.Adapter<TimeslotAdapter.Myholder>() {

    var c=-1
    override fun onBindViewHolder(p0: Myholder, p1: Int) {
        p0.tv!!.text = lis[p1]
        if(c==-1)
        {

        }
        else{
            if(c==p0.adapterPosition)
            {
                p0.tv!!.setBackgroundColor(Color.parseColor("#7ebe43"))
                p0.tv!!.setTextColor(Color.parseColor("#FFFFFF"))

            }
            else{
                p0.tv!!.setBackgroundColor(Color.parseColor("#f0f4f8"))
                p0.tv!!.setTextColor(Color.parseColor("#748a9d"))
            }
        }
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TimeslotAdapter.Myholder {
        var inf = LayoutInflater.from(activity)
        var v = inf.inflate(R.layout.timeslot_view, p0, false)
        var myholder = TimeslotAdapter.Myholder(v)

        v.setOnClickListener {
            myholder.tv!!.setBackgroundColor(Color.parseColor("#7ebe43"))
            myholder.tv!!.setTextColor(Color.parseColor("#FFFFFF"))
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

        var tv: TextView? = null


        init {
            tv = v.tv
        }




    }

    fun getSelected():String
    {
        if(c!=-1)
        {
            return lis.get(c)
        }

        return "gone"

    }
}
