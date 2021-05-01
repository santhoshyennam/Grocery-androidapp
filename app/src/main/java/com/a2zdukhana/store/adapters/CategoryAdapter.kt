package com.a2zdukhana.store.adapters


import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.a2zdukhana.store.R
import com.a2zdukhana.store.classes.CategoryClass
import kotlinx.android.synthetic.main.categories_grid.view.*


class CategoryAdapter(var activity: Context, var lis:MutableList<CategoryClass>,var ac:String): BaseAdapter(){
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var inf=LayoutInflater.from(activity)
        var v = inf.inflate(R.layout.categories_grid,null)
        Glide.with(activity).load(lis[p0].image).placeholder(R.mipmap.ic_launcher).into(v.iview)
        if(ac.equals("home"))
        {
          var name = lis[p0].name.substring(2)
            v.name.text = name[0].toUpperCase().toString()+name.substring(1)
        }
        else
        v.name.text = lis[p0].name[0].toUpperCase().toString()+lis[p0].name.substring(1)
        return v
    }

    override fun getItem(p0: Int): Any {
        return 0
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return lis.size
    }

}
