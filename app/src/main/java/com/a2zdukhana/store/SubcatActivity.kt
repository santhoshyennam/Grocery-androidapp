package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.a2zdukhana.store.adapters.CategoryAdapter
import com.a2zdukhana.store.classes.CategoryClass
import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_subcat.*
import kotlinx.android.synthetic.main.my_account.back

@Suppress("DEPRECATION")
class SubcatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subcat)
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        }
        var grid = FirebaseDatabase.getInstance().getReference("categories").child(intent.getStringExtra("cato")!!).child("subcategory")
        grid.addListenerForSingleValueEvent(
            object : ValueEventListener {
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
                        gridview.adapter = CategoryAdapter(this@SubcatActivity,lis,"subcat")
                        gridview.visibility = View.VISIBLE
                        spin_kit.visibility = View.GONE
                        gridview.onItemClickListener = object : AdapterView.OnItemClickListener{
                            override fun onItemClick(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                //p1!!.setBackgroundColor(Color.WHITE)
                                startActivity(Intent(this@SubcatActivity,ItemsActivity::class.java).putExtra("subcato",lis[p2].name)
                                    .putExtra("cato",intent.getStringExtra("cato")).putExtra("activity","sub").putExtra("pos",p2.toString()))
                                overridePendingTransition(R.anim.slide_in_right,
                                    R.anim.slide_out_left);

                                // Toast.makeText(this@SubcatActivity,lis[p2].name, Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                }

            }
        )

        cart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java).putExtra("ac","other"))

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        var user =FirebaseAuth.getInstance().currentUser
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



    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);


    }
    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()
        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            startActivity(Intent(this,MainActivity::class.java))
        }

    }
}