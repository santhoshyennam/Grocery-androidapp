package com.a2zdukhana.store.adapters

import android.content.Context
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import com.a2zdukhana.store.R
import com.a2zdukhana.store.classes.SliderItem1
import kotlinx.android.synthetic.main.image_slider.view.*


class SliderAdapter(var activity:Context,var lis:MutableList<SliderItem1>) : PagerAdapter() {
    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view === o
    }


    override fun getCount(): Int {
        return lis.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val inflater = LayoutInflater.from(activity)
        val layoutScreen = inflater.inflate(R.layout.image_slider, null)

        layoutScreen.desc.text = lis[position].desc
        layoutScreen.image.setImageDrawable(ContextCompat.getDrawable(activity,lis[position].im))

        container.addView(layoutScreen)

        return layoutScreen
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as View)

    }
}