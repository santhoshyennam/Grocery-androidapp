package com.a2zdukhana.store.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.a2zdukhana.store.*
import com.a2zdukhana.store.adapters.CategoryAdapter
import com.a2zdukhana.store.adapters.SliderAdapterExample
import com.a2zdukhana.store.classes.CategoryClass
import com.a2zdukhana.store.classes.SliderItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.home_frag.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class HomeFrag : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.home_frag, null)
        var sliderView = v.sliderView
        sliderView.startAutoCycle();
        //sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();


        var banner = FirebaseDatabase.getInstance().getReference("homepage").child("banner")
        banner.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var chi = p0.children
                        var lis = mutableListOf<SliderItem>()
                        chi.forEach {
                            var chi1 = it.children
                            //var im=""
                            var map = HashMap<String,String>()
                            map.put("itemkey","itemkey")
                            chi1.forEach {
                               map.put(it.key.toString(),it.value.toString())
                            }
                            var s= SliderItem(map.getValue("image"),"",map.getValue("category")
                                ,map.getValue("subcategory"),map.getValue("itemkey"))
                            lis.add(s)
                        }
                        v.sliderView.setSliderAdapter(SliderAdapterExample(activity!!,lis))
                        v.spin_kit.isIndeterminate = false
                        v.spin_kit.visibility = View.GONE
                        v.real.visibility = View.VISIBLE
                    }
                }

            }
        )


        var todayimage = FirebaseDatabase.getInstance().getReference("homepage").child("today")
        var dealimage = FirebaseDatabase.getInstance().getReference("homepage").child("deal")
        todayimage.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var chi = p0.children
                        var map = HashMap<String,String>()
                        chi.forEach {
                            map.put(it.key.toString(),it.value.toString())
                        }
                        Glide.with(activity!!).load(map.getValue("image")).into(v.timage)
                        v.timage.setOnClickListener {
                            activity!!.startActivity(Intent(activity!!,SliderItems::class.java)
                                .putExtra("category",map.getValue("category"))
                                .putExtra("subcategory",map.getValue("subcategory"))
                                .putExtra("itemkey",map.getValue("itemkey")))

                            activity!!.overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);

                        }
                    }
                }

            }
        )
        dealimage.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var map = HashMap<String,String>()
                        var chi = p0.children
                        chi.forEach {
                            when(it.key)
                            {
                                "timer"->{
                                    var x = it.value.toString()
                                    v.expire.text = ""
                                    val formatter =
                                        SimpleDateFormat("dd.MM.yyyy, HH:mm")
                                    formatter.setLenient(false)

                                    val curDate = Date()
                                    val curMillis: Long = curDate.getTime()
                                    //val curTime: String = formatter.format(curDate)

                                    val fTime =x
                                    val fDate: Date = formatter.parse(fTime)!!
                                    val fmillis: Long = fDate.getTime()
                                    //Toast.makeText(activity,(fmillis-curMillis).toString(),Toast.LENGTH_LONG).show()

                                    var limit = (fmillis-curMillis)/1000
                                    var timer=object : CountDownTimer(limit*1000, 1000) {
                                        override fun onTick(millisUntilFinished: Long) {
                                            //expire.setText("expires in "+java.lang.String.valueOf(x))
                                            val s: Long = limit % 60
                                            val m: Long = limit / 60 % 60
                                            val h: Long = (limit / (60 * 60)) % 24
                                            var f1= String.format("%dhr %02dmin %02dsec", h, m, s)
                                            v.expire.setText(""+f1)

                                            limit--
                                        }

                                        override fun onFinish() {
                                            v.expire.setText("Finished")
                                        }
                                    }
                                    timer.start()


                                }
                                "image"-> Glide.with(activity!!).load(it.value.toString()).into(v.dimage)
                                else->map.put(it.key.toString(),it.value.toString())

                            }

                            v.dimage.setOnClickListener {
                                if(v.expire.text.equals("Finished"))
                                {
                                    Toast.makeText(activity,"Timed out",Toast.LENGTH_LONG).show()
                                }
                                else
                                {
                                    activity!!.startActivity(Intent(activity!!,SliderItems::class.java)
                                        .putExtra("category",map.getValue("category"))
                                        .putExtra("subcategory",map.getValue("subcategory"))
                                        .putExtra("itemkey",map.getValue("itemkey")))
                                    activity!!.overridePendingTransition(R.anim.slide_in_right,
                                        R.anim.slide_out_left);
                                }

                            }
                        }
                    }
                }

            }
        )
        var grid = FirebaseDatabase.getInstance().getReference("categories")
        grid.addValueEventListener(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var chi = p0.children
                        var lis = mutableListOf<CategoryClass>()
                        chi.forEach {
                            var chi1 = it.children
                            var map = HashMap<String,String>()
                            map.put("name",it.key.toString())
                            chi1.forEach {
                                when(it.key){
                                 "image"-> {
                                     map.put(it.key.toString(), it.value.toString())
                                 }
                                }
                            }
                          var l = CategoryClass(map.getValue("name"),map.getValue("image"))
                            lis.add(l)
                        }
                        v.gridview.adapter = CategoryAdapter(activity!!,lis,"home")
                        v.gridview.onItemClickListener = object : AdapterView.OnItemClickListener{
                            override fun onItemClick(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                //p1!!.setBackgroundColor(Color.WHITE)
                                var db = FirebaseDatabase.getInstance().getReference("categories").
                                child(lis[p2].name).child("subcategory").child("no subcategory")
                                db.addListenerForSingleValueEvent(
                                    object : ValueEventListener
                                    {
                                        override fun onCancelled(p0: DatabaseError) {

                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                                if(!p0.exists())
                                                {
                                                    startActivity(Intent(activity,SubcatActivity::class.java).putExtra("cato",lis[p2].name))
                                                    activity!!.overridePendingTransition(R.anim.slide_in_right,
                                                        R.anim.slide_out_left);

                                                }
                                                else
                                                {
                                                    startActivity(Intent(activity,ItemsActivity::class.java).putExtra("cato",lis[p2].name)
                                                        .putExtra("activity","home").putExtra("subcato","no subcategory"))

                                                    activity!!.overridePendingTransition(R.anim.slide_in_right,
                                                        R.anim.slide_out_left);
                                                }
                                        }

                                    }
                                )
                                //startActivity(Intent(activity,SubcatActivity::class.java).putExtra("cato",lis[p2].name))

                                //Toast.makeText(activity,lis[p2].name,Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                }

            }
        )

        return v
    }
}