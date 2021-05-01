package com.a2zdukhana.store

import com.a2zdukhana.store.adapters.ItemsAdapter
import com.a2zdukhana.store.classes.CostClass
import com.a2zdukhana.store.classes.ItemClass
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.back
import kotlinx.android.synthetic.main.activity_search.cart
import kotlinx.android.synthetic.main.activity_search.ccount

class SearchActivity : AppCompatActivity() {
    var lis = mutableListOf<ItemClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        var lm= LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL,false)
        rview.layoutManager = lm
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        }

        //start

        var word = ""

        var db = FirebaseDatabase.getInstance().getReference("categories")

        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
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
                                                        lis.add(i)

                                                }

                                            }
                                        }
                                    }

                                }
                            }
                        }


                    }
                   /* if(lis.size>0)
                    {
                        //Toast.makeText(activity,""+lis.size,Toast.LENGTH_LONG).show()
                        rview.adapter = ItemsAdapter(this@SearchActivity,lis)
                    }
                    else
                    {
                        Toast.makeText(this@SearchActivity,"no item is found", Toast.LENGTH_LONG).show()
                        rview.visibility = View.GONE
                    }*/


                }


            }
        )

        cart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java).putExtra("ac","other"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }

        var user = FirebaseAuth.getInstance().currentUser
        if(user==null)
            ccount.text ="0"
        else
        {
            var uid = FirebaseAuth.getInstance().uid
            var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("cart")

            db.addValueEventListener(
                object : ValueEventListener
                {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists())
                        {
                            var c=0
                            var chi = p0.children
                            chi.forEach {
                                c++
                            }
                            ccount.text = c.toString()
                        }
                        else
                        {
                            ccount.setText("0")
                        }
                    }

                }
            )
        }


        //end
        searchhere.addTextChangedListener(
            object : TextWatcher
            {
                override fun afterTextChanged(p0: Editable?) {
                    if(p0!!.toString().length>2)
                    changeRecycler(p0!!.toString())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

            }
        )



        searchhere.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                /*if(!search1.text.toString().equals("")) {
                    var fragment: Fragment = SearchFrag()
                    var bundle = Bundle()
                    bundle.putString("word", search1.text.toString())
                    fragment.arguments = bundle
                    //if (x == 0)
                      //  supportFragmentManager.beginTransaction()
                      //      .replace(R.id.fragment, fragment).addToBackStack(null).commit()
                   // else
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, fragment).commit()
                    //x++
                }
                else
                {
                   Toast.makeText(this@MainActivity,"please enter item name",Toast.LENGTH_LONG).show()

                }

                return true*/

                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event != null &&
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        if(!searchhere.text.toString().equals("")) {
                            changeRecycler(searchhere.text.toString())
                        }
                        else
                        {
                            Toast.makeText(this@SearchActivity,"please enter item name",Toast.LENGTH_LONG).show()

                        }

                        return true; // consume.
                    }
                }

                return false
            }
        })

    }

    private fun changeRecycler(s: String) {
        var x= mutableListOf<ItemClass>()
        var i=0
        while(i<lis.size){
            if(lis[i].name.contains(s,true)
                ||lis[i].desc.contains(s,true)||
                lis[i].subcategory.contains(s,true))
                x.add(lis[i])
            i++
        }
        if(x.size>0)
        {
            //Toast.makeText(activity,""+lis.size,Toast.LENGTH_LONG).show()
            rview.visibility = View.VISIBLE
            var adapter= ItemsAdapter(this@SearchActivity,x)
            adapter.updateList(x)
            rview.adapter = adapter
            //rview.adapter = ItemsAdapter(this@SearchActivity,x)
        }
        else
        {
            Toast.makeText(this@SearchActivity,"no item is found", Toast.LENGTH_LONG).show()
            rview.visibility = View.GONE
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right)

    }
}