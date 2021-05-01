@file:Suppress("DEPRECATION")

package com.a2zdukhana.store.fragments


import android.app.ProgressDialog
import com.a2zdukhana.store.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zdukhana.store.adapters.ItemsAdapter
import com.a2zdukhana.store.classes.CostClass
import com.a2zdukhana.store.classes.ItemClass
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.items_frag.view.*


@Suppress("DEPRECATION")
class ItemsFrag : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.items_frag, null)
        var lm= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        v.rview.layoutManager = lm
       var cato = arguments!!.getString("cato")
        var subcat = arguments!!.getString("subcat")
       // var ac = arguments!!.getString("activity")

            nonhome(cato!!,subcat!!,v)

        //nonhome("11meat","Chicken",v)

        return v
    }


    private fun nonhome(cato:String,subcat:String,v:View)
    {

        var db = FirebaseDatabase.getInstance().getReference("categories").child(cato).child("subcategory").child(subcat)
        var pd = ProgressDialog(activity)
        pd.setTitle("Loading..")
        pd.show()
        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        var chi = p0.children
                        var lis = mutableListOf<ItemClass>()
                        chi.forEach {
                            when(it.key){
                                "image"->{

                                }
                                else->
                                {
                                    var chi1 = it.children
                                    var map = HashMap<String,String>()
                                    map.put("key",it.key.toString())
                                    var c = mutableListOf<CostClass>()
                                    chi1.forEach {
                                        when(it.key)
                                        {
                                            "quantity"->{
                                                var chi2 = it.children
                                                var map1 = HashMap<String,String>()
                                                map1.put("available","1")
                                                chi2.forEach {
                                                    map1.put("key",it.key.toString())
                                                    var chi3 = it.children
                                                    chi3.forEach {
                                                        map1.put(it.key.toString(),it.value.toString())
                                                    }
                                                    var co = CostClass(map1.getValue("quantity"),map1.getValue("cost"),map1.getValue("discount"),
                                                        map1.getValue("key"),map1.getValue("available"))
                                                    c.add(co)
                                                }

                                            }
                                            else->{
                                                map.put(it.key.toString(),it.value.toString())

                                            }
                                        }
                                    }
                                    var i = ItemClass(map.getValue("name"),map.getValue("image"),c,map.getValue("desc"),
                                        cato,subcat,"1",map.getValue("key"))
                                    lis.add(i)
                                }
                            }
                        }
                        v.rview.adapter = ItemsAdapter(activity!!,lis)
                        pd.dismiss()
                        v.rview.visibility = View.VISIBLE
                        v.spin_kit.visibility = View.GONE

                    }
                }

            }
        )
    }
}