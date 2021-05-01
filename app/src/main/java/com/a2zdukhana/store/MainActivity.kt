package com.a2zdukhana.store

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.a2zdukhana.store.fragments.HomeFrag
import com.a2zdukhana.store.fragments.MaintainFrag
import android.app.ProgressDialog
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.maintainance_view.view.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val END_SCALE = 0.7f
    /*private val bottomNavMethod =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            var fragment: Fragment? = null
            when (item.itemId) {
                R.id.home -> {
                    fragment = HomeFrag()
                }
                R.id.menu->{
                    fragment = CatogoriesFrag()
                }
                R.id.offer->{
                    fragment = OffersFrag()
                }
                R.id.basket->{
                    fragment = BasketFrag()
                }
                R.id.search->{
                    search1.requestFocus()
                    search1.isFocusableInTouchMode = true
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(search1, InputMethodManager.SHOW_FORCED)
                }
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, fragment!!).addToBackStack(null).commit()
            true
        }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var pd = ProgressDialog(this@MainActivity)
        pd.setTitle("Please wait..")
        pd.setCanceledOnTouchOutside(false)
        pd.show()
        var db =FirebaseDatabase.getInstance().getReference("enable")
        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.value.toString().equals("0"))
                    {
                        var fragmentTransaction = supportFragmentManager.beginTransaction()
                        fragmentTransaction.add(R.id.fragment, MaintainFrag())
                        fragmentTransaction.commit()
                        var alert= AlertDialog.Builder(this@MainActivity)
                        var v1 =layoutInflater.inflate(R.layout.maintainance_view,null)
                        alert.setView(v1)
                        var alert1 = alert.create()
                        alert1.setCanceledOnTouchOutside(false)
                        alert1.show()
                        v1.done.setOnClickListener {
                            finish()
                        }
                        pd.dismiss()
                    }
                    else
                    {
                        // bottomnv.setOnNavigationItemSelectedListener(bottomNavMethod)
                        loadFirstFragment()
                        nav_view.setNavigationItemSelectedListener(this@MainActivity)
                        animateNavigationDrawer()
                        clickEvents()
                        loadUserDetails()
                        pd.dismiss()

                    }
                }

            }
        )


    }

    private fun loadFirstFragment() {
        var fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fragment,HomeFrag())
        fragmentTransaction.commit()
        //        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
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
                            "fullname"->mname.text = "Hi, "+it.value.toString()
                            "mobile"-> email.text = it.value.toString()
                            "email"->email.text = it.value.toString()
                           "address"->{
                                var map = HashMap<String,String>()
                               var chi2 = it.children
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

    private  fun clickEvents()
    {
        var db = FirebaseDatabase.getInstance().getReference("myinformation")
        db.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var twh = ""
                    var tcall = ""
                    chi.forEach {
                        when (it.key) {
                            "whatsapp" -> twh = it.value.toString()
                            "phone1" -> tcall = it.value.toString()


                        }
                    }
                    whatsapp.setOnClickListener {
                        val phoneNumber = twh
                        val url = "https://api.whatsapp.com/send?phone=$phoneNumber"
                        try {
                            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        } catch (e: PackageManager.NameNotFoundException) {
                            Toast.makeText(this@MainActivity, "Whatsapp is not installed in your phone.", Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                        }
                    }
                    call.setOnClickListener {
                        var i = Intent(Intent.ACTION_DIAL)
                        i.data=Uri.parse("tel:"+tcall)
                        startActivity(i)
                    }




                }
            })



        noti.setOnClickListener {
            startActivity(Intent(this,NotificationActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }
        share.setOnClickListener {
            if(drawer_layout.isDrawerVisible(GravityCompat.START))
                drawer_layout.closeDrawer(GravityCompat.START);
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name")
            var shareMessage = "\nLet me recommend you this application\n\n"
            shareMessage =
                """
                ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
                
                
                """.trimIndent()

            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
        home.setOnClickListener {

            var fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment,HomeFrag())
            fragmentTransaction.commit()
                drawer_layout.closeDrawer(GravityCompat.START);
        }
        contact.setOnClickListener {
             startActivity(Intent(this@MainActivity,Contactus::class.java))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);



        }

        rate.setOnClickListener {
            val builder =
                android.app.AlertDialog.Builder(this)
            builder.setMessage("Rate us and please share your valuable feedback on our playstore page.")
                .setPositiveButton(
                    "Go to Playstore"
                ) { dialog, id ->
                    //do things
                    val rateIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + this.getPackageName())
                    )
                    startActivity(rateIntent)
                }
            val alert = builder.create()
            alert.show()
        }
        myprofile.setOnClickListener {
            startActivity(Intent(this@MainActivity,MyAccountActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }
        cart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java).putExtra("ac","main"))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }

        nav_button.setOnClickListener {
            if(drawer_layout.isDrawerVisible(GravityCompat.START))
                drawer_layout.closeDrawer(GravityCompat.START);
            else drawer_layout.openDrawer(GravityCompat.START);
        }
        myorders.setOnClickListener {
            startActivity(Intent(this,MyOrdersActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        var listener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                when(p0!!.id){
                    R.id.ledit->{
                        startActivity(Intent(this@MainActivity,MyAddressActivity::class.java))
                        overridePendingTransition(R.anim.slide_in_right,
                            R.anim.slide_out_left);

                    }
                    R.id.profile->{
                        val c = Pair.create<View, String>(imageView, "user")
                        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity,c)
                        var user = FirebaseAuth.getInstance().currentUser
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            startActivity(Intent(this@MainActivity,MyAccountActivity::class.java), options.toBundle())
                        }
                        else
                            startActivity(Intent(this@MainActivity,MyAccountActivity::class.java))
                    }

                }
            }
        }
        ledit.setOnClickListener(listener)
        profile.setOnClickListener(listener)
       // var x =0

       /* search1.setOnEditorActionListener(object : TextView.OnEditorActionListener {
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
                            if(!search1.text.toString().equals("")) {
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

                            return true; // consume.
                        }
                    }

                return false
            }
        })
        search1.addTextChangedListener(
            object: TextWatcher
            {
                override fun afterTextChanged(p0: Editable?) {
                                    }
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(search1.text.toString().length>2) {
                        var fragment: Fragment = SearchFrag()
                        var bundle = Bundle()
                        bundle.putString("word", search1.text.toString())
                        fragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, fragment).commit()

                    }
                    else if(search1.text.toString().length>0)
                    {

                    }
                    else
                    {
                        //Toast.makeText(this@MainActivity,"please enter item name",Toast.LENGTH_LONG).show()
                        var fragment: Fragment = HomeFrag()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment, fragment).commit()


                    }

                }
            })*/

        search1.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()

            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }
    }


   override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        /*when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }*/

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        drawer_layout.setScrimColor(resources.getColor(R.color.colorPrimary));
        drawer_layout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                // Scale the View based on current slide offset
                val diffScaledOffset = slideOffset * (1 - END_SCALE)
                val offsetScale = 1 - diffScaledOffset
                contentView.setScaleX(offsetScale)
                contentView.setScaleY(offsetScale)

                // Translate the View, accounting for the scaled width
                val xOffset = drawerView.getWidth() * slideOffset
                val xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2
                val xTranslation = xOffset - xOffsetDiff
                contentView.setTranslationX(xTranslation)
            }
        })

    }

    override fun onStart() {
        super.onStart()
        var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(cm.activeNetworkInfo==null)
        {
            startActivity(Intent(this,NetConnection::class.java))
        }
    }

}
