package com.a2zdukhana.store.adapters

import com.a2zdukhana.store.ItemsActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.a2zdukhana.store.R
import com.a2zdukhana.store.SliderItems
import com.a2zdukhana.store.classes.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.ad_slider.view.*


class SliderAdapterExample(var ac: Context, var lis:MutableList<SliderItem>) : SliderViewAdapter<SliderAdapterExample.SliderAdapterVH>() {

    // var mSliderItems = mutableListOf<SliderItem>()


    /* fun renewItems(sliderItems: MutableList<SliderItem>) {
         this.mSliderItems = sliderItems
         notifyDataSetChanged()
     }


     fun deleteItem(position: Int) {
         this.mSliderItems.removeAt(position)
         notifyDataSetChanged()
     }


     fun addItem(sliderItem: SliderItem) {
         this.mSliderItems.add(sliderItem)
         notifyDataSetChanged()
     }*/


    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate = LayoutInflater.from(ac).inflate(R.layout.ad_slider, null)
        var myholder = SliderAdapterVH(inflate)
        return myholder

    }

    override fun getCount(): Int {
        return lis.size
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {

        Glide.with(ac).load(lis[position].im).into(viewHolder!!.imageGifContainer)
        viewHolder.imageGifContainer.setOnClickListener {
            if(!lis[position].itemkey.equals("itemkey"))
            {
                ac.startActivity(Intent(ac,SliderItems::class.java)
                    .putExtra("category",lis[position].category)
                    .putExtra("subcategory",lis[position].subcategory)
                    .putExtra("itemkey",lis[position].itemkey))
            }
            else{
                ac.startActivity(Intent(ac,ItemsActivity::class.java)
                    .putExtra("cato",lis[position].category)
                    .putExtra("subcato",lis[position].subcategory)
                )
            }


        }


    }


    inner class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {


        var imageGifContainer: ImageView



        init {

            imageGifContainer = itemView.image


        }

    }

}