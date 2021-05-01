package com.a2zdukhana.store.fragments


import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.a2zdukhana.store.R
import com.a2zdukhana.store.adapters.ItemsAdapter
import com.a2zdukhana.store.classes.CostClass
import com.a2zdukhana.store.classes.ItemClass
import android.annotation.SuppressLint
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.items_frag.view.*

@Suppress("DEPRECATION")
class SearchFrag : Fragment() {
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var v = inflater.inflate(R.layout.items_frag, null)
        var lm= LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
        v.rview.layoutManager = lm
         var word = arguments!!.getString("word")!!.toLowerCase()

        var db = FirebaseDatabase.getInstance().getReference("categories")
        var pd = ProgressDialog(activity)
        pd.setTitle("Please Wait..")
            pd.show()
        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var lis = mutableListOf<ItemClass>()
                    chi.forEach {
                        var map = HashMap<String, String>()
                        var chi7 = it.children
                        map.put("category",it.key.toString())

                                chi7.forEach {
                                    when (it.key) {
                                        "subcategory" -> {
                                            var chi2 = it.children
                                            chi2.forEach {
                                                map.put("subcat",it.key.toString())

                                                        var chi3 = it.children
                                                        chi3.forEach {
                                                            when(it.key){
                                                                "image"->{}
                                                                else->{
                                                            var c = mutableListOf<CostClass>()
                                                            map.put("key",it.key.toString())
                                                            var chi4 = it.children
                                                            chi4.forEach {
                                                                when (it.key) {
                                                                    "quantity" -> {
                                                                        var map1 = HashMap<String, String>()
                                                                        var chi5 = it.children
                                                                        chi5.forEach {
                                                                            var chi6 = it.children
                                                                            map1.put("key",it.key.toString())
                                                                            map1.put("available","1")
                                                                            chi6.forEach {
                                                                                map1.put(
                                                                                    it.key.toString(),
                                                                                    it.value.toString()
                                                                                )
                                                                            }
                                                                            var co = CostClass(
                                                                                map1.getValue("quantity"),
                                                                                map1.getValue("cost"),
                                                                                map1.getValue("discount"),
                                                                                map1.getValue("key"),
                                                                                map1.getValue("available")
                                                                            )
                                                                            c.add(co)
                                                                        }
                                                                    }
                                                                    else -> {
                                                                        map.put(
                                                                            it.key.toString(),
                                                                            it.value.toString()
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                            var i = ItemClass(
                                                                map.getValue("name"),
                                                                map.getValue("image"),
                                                                c,
                                                                map.getValue("desc"),
                                                                map.getValue("category"),
                                                                map.getValue("subcat"),
                                                                "1",map.getValue("key")
                                                            )
                                                            if(map.getValue("name").contains(word,true)
                                                                ||map.getValue("desc").contains(word,true)||
                                                                map.getValue("subcat").contains(word,true))
                                                                lis.add(i)

                                                        }

                                                     }
                                                        }
                                            }

                                        }
                                    }
                                }


                    }
                    if(lis.size>0)
                    {
                        //Toast.makeText(activity,""+lis.size,Toast.LENGTH_LONG).show()
                        v.rview.adapter = ItemsAdapter(activity!!,lis)
                        v.spin_kit.visibility=View.GONE
                            pd.dismiss()
                    }
                    else
                    {
                        Toast.makeText(activity,"no item is found",Toast.LENGTH_LONG).show()
                        v.rview.visibility = View.GONE
                        v.spin_kit.visibility=View.GONE
                        pd.dismiss()
                    }


                }


            }
        )

        return v

    }



}