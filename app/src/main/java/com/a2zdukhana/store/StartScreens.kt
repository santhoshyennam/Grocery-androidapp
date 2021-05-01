package com.a2zdukhana.store

import android.content.Intent
import android.os.Build
import android.os.Bundle

import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.a2zdukhana.store.adapters.SliderAdapter
import com.a2zdukhana.store.classes.SliderItem1
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_start_screens.*


class StartScreens : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_screens)
        var lis = mutableListOf<SliderItem1>()
        var i=0
        var im = arrayOf(R.drawable.ic_page1,R.drawable.ic_page2,R.drawable.ic_page3)
        var desc = arrayOf("Quickly search and add grocery items with one click to your cart",
                "Collect points with each purchase and exchange them  for rewards and discounts",
                "We offer amazing and exclusive rewards and coupons that you can only find in here!")
        while(i<3)
        {
            var s= SliderItem1(im[i],desc[i])
            lis.add(s)
            i++
        }

        screen_viewpager.adapter = SliderAdapter(this@StartScreens,lis)
        tab_indicator.setupWithViewPager(screen_viewpager)
        getstart.setOnClickListener {
            val button = Pair.create<View, String>(getstart, "button")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@StartScreens, button)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@StartScreens,MainActivity::class.java),options.toBundle())
                finish()
            }
        }
        tab_indicator.addOnTabSelectedListener(object : TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
            override fun onTabSelected(tab: TabLayout.Tab) {

                if (tab.position == lis.size - 1) {

                    loaddLastScreen()

                }
                else
                {
                    loadStart()
                }


            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })

            getstart.visibility = View.GONE

    }

    private fun loadStart() {
        getstart.visibility = View.GONE

    }

    private fun loaddLastScreen() {
        getstart.visibility = View.VISIBLE
    }
}