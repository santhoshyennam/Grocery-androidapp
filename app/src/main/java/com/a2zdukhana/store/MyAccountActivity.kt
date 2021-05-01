package com.a2zdukhana.store

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.my_account.*
import kotlinx.android.synthetic.main.my_account.address
import kotlinx.android.synthetic.main.my_account.email

@Suppress("DEPRECATION")
class MyAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_account)
        logout.setOnClickListener {

            var alert= AlertDialog.Builder(this@MyAccountActivity)
            alert.setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("logout",
                    DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                        FirebaseAuth.getInstance().signOut()
                        val c = Pair.create<View, String>(logout1, "email")
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(this,"you have logged out",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@MyAccountActivity,MainActivity::class.java))
                        finish()
                        overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                })
            var al = alert.create()
            al.show()


        }
        daddress.setOnClickListener {
            val c = Pair.create<View, String>(change, "change")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyAccountActivity,MyAddressActivity::class.java), options.toBundle())

            }
            else
            startActivity(Intent(this@MyAccountActivity,MyAddressActivity::class.java))
        }
        orders.setOnClickListener {
            val c = Pair.create<View, String>(change, "change")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyAccountActivity,MyOrdersActivity::class.java), options.toBundle())
            }
            else
            startActivity(Intent(this@MyAccountActivity,MyOrdersActivity::class.java))
        }
        change.setOnClickListener {
            val c = Pair.create<View, String>(change, "change")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyAccountActivity,MyAddressActivity::class.java), options.toBundle())
            }
            else
            startActivity(Intent(this@MyAccountActivity,AddAddressActivity::class.java))
        }
       /* edit.setOnClickListener {
            startActivity(Intent(this@MyAccountActivity,EditDetailsActivity::class.java))
        }*/
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }

        rewards.setOnClickListener {
            val c = Pair.create<View, String>(change, "change")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyAccountActivity,MyRewards::class.java), options.toBundle())
            }
            else
                startActivity(Intent(this@MyAccountActivity,MyRewards::class.java))
        }
        ratings.setOnClickListener {
            val c = Pair.create<View, String>(change, "change")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyAccountActivity,MyRatings::class.java), options.toBundle())
            }
            else
                startActivity(Intent(this@MyAccountActivity,MyRatings::class.java))
        }
        changemobile.setOnClickListener {
            val c = Pair.create<View, String>(change, "change")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyAccountActivity,Addmobile::class.java), options.toBundle())
            }
            else
                startActivity(Intent(this@MyAccountActivity,Addmobile::class.java))
        }
        changepass.setOnClickListener {
            val c = Pair.create<View, String>(change, "change")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,c)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(Intent(this@MyAccountActivity,ChangePassword::class.java), options.toBundle())
            }
            else
                startActivity(Intent(this@MyAccountActivity,ChangePassword::class.java))

        }
        loadUserDetails()
    }
    private fun loadUserDetails() {
        var uid = FirebaseAuth.getInstance().uid
        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString())
        dbase.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    chi.forEach{
                        when(it.key)
                        {
                            "fullname"->name.text = "Hi, "+it.value.toString()
                            "mobile"-> mobile.text = it.value.toString()
                            "email"->email.text = it.value.toString()
                            "address"->{
                                var chi2 =it.children
                                var map = HashMap<String,String>()
                                chi2.forEach {
                                    map.put(it.key.toString(),it.value.toString())
                                }
                                var addre = map.getValue("area")+","+map.getValue("landmark")+","+map.getValue("pincode")
                                if(addre.length>28)
                                {
                                    address.text = addre.substring(0,28)+".."
                                }
                                else
                                address.text = addre
                            }
                        }
                    }
                }

            }
        )
    }
    override fun onStart() {
        super.onStart()

        var user  = FirebaseAuth.getInstance().currentUser
        if(user==null)
        {
            startActivity(Intent(this,Mobilelogin::class.java))
            finish()
        }

            var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(cm.activeNetworkInfo==null)
            {
                startActivity(Intent(this,NetConnection::class.java))
            }


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);


    }
}