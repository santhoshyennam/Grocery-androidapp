package com.a2zdukhana.store

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.my_location.*
import kotlinx.android.synthetic.main.my_location.back
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class MyAddressActivity : AppCompatActivity()  , GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    ResultCallback<LocationSettingsResult> {
    private lateinit var mMap: GoogleMap
    protected var mGoogleApiClient: GoogleApiClient? = null
    protected var locationRequest: LocationRequest? = null
    var REQUEST_CHECK_SETTINGS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_location)
        var lm= LinearLayoutManager(this@MyAddressActivity, LinearLayoutManager.VERTICAL,false)
       // rview.layoutManager = lm
        var uid = FirebaseAuth.getInstance().uid
        //var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("mylocation")
       /* db.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    var chi = p0.children
                    var lis = mutableListOf<LocationClass>()
                    chi.forEach {
                      var chi1 = it.children
                        var map = HashMap<String,String>()
                        chi1.forEach {
                            map.put(it.key.toString(),it.value.toString())
                        }
                        var l = LocationClass(map.getValue("locationname"),map.getValue("address"),map.getValue("area"),
                            map.getValue("landmark"),map.getValue("pincode"),map.getValue("lati"),map.getValue("longi"))
                        lis.add(l)
                        }
                    if(lis.size==0)
                        tloc.visibility=View.GONE
                    rview.adapter = LocationAdapter(this@MyAddressActivity,lis)
                }

            })*/

        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("address")

        dbase.addValueEventListener(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        l1.visibility = View.VISIBLE
                        var chi = p0.children
                        var map = HashMap<String,String>()
                        chi.forEach {
                            when(it.key)
                            {
                                "locationname"->{
                                    name.text = it.value.toString()
                                }
                                else->{
                                    map.put(it.key.toString(),it.value.toString())
                                }
                            }
                        }

                        address.text = "Address: "+map.getValue("address")+"\n"+"Area: "+map.getValue("area")+"\n"+
                                "Landmark: "+map.getValue("landmark")+"\n"+
                                "Pincode: "+map.getValue("pincode")
                        email.text = map.getValue("mobile")
                    }
                    else
                    {
                        l1.visibility = View.GONE
                    }
                    spin_kit.visibility = View.GONE

                }

            }
        )

        add.setOnClickListener {
            var i = Intent(this@MyAddressActivity,AddAddressActivity::class.java)
            i.putExtra("lati","0")
            i.putExtra("longi","0")
            startActivity(i)

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);


        }
        back.setOnClickListener {
            finish()

            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


        }
        myloc.setOnClickListener {
            var status = ContextCompat.checkSelfPermission(
                this@MyAddressActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            if(status== PackageManager.PERMISSION_GRANTED){
                if(isLocationEnabled(this@MyAddressActivity))
                {
                    setLocation()
                }
                else
                {
                    initializeLocation()
                }

            }else{
                ActivityCompat.requestPermissions(this@MyAddressActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    111)
            }
        }

        edit.setOnClickListener {
            startActivity(Intent(this,SelectAddress::class.java))

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        l1.setOnClickListener {
            startActivity(Intent(this,SelectAddress::class.java))

            overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);

        }
        /*delete.setOnClickListener {

            var alert= AlertDialog.Builder(this@MyAddressActivity)
            alert.setMessage("Do you want to Exit?")
                .setCancelable(false)
                .setPositiveButton("yes",
                    DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                   dbase.removeValue()
                })
                .setNegativeButton("no",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.cancel()
                })
            var al = alert.create()
            al.show()

        }*/
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

    //start
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            if(isLocationEnabled(this@MyAddressActivity))
            {
                setLocation()

            }
            else
            {
                initializeLocation()
            }

        }else{
            Toast.makeText(this@MyAddressActivity,
                "App Can't read Location info...",
                Toast.LENGTH_LONG).show()
        }
    }



    private fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }


    override fun onConnected(p0: Bundle?) {

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(
            mGoogleApiClient,
            builder.build()
        )

        result.setResultCallback(this)
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

    override fun onResult(@NonNull locationSettingsResult: LocationSettingsResult) {
        val status: Status = locationSettingsResult.getStatus()
        when (status.getStatusCode()) {
            LocationSettingsStatusCodes.SUCCESS -> {
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                 //  Location settings are not satisfied. Show the user a dialog
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(
                        this@MyAddressActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (e: IntentSender.SendIntentException) {

                    //failed to show
                }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {
                setLocation()
                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
                finish()
            }

        }
    }
    private fun initializeLocation() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).build()
        mGoogleApiClient!!.connect()
        locationRequest = LocationRequest.create()
        locationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest!!.setInterval(30 * 1000)
        locationRequest!!.setFastestInterval(5 * 1000)
    }
    private fun setLocation() {
        var pd = ProgressDialog(this@MyAddressActivity)
        pd.setMessage("Accessing GPS..")
        pd.show()
        pd.setCancelable(false)
        var lManager: LocationManager = getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
        /*lManager.getLastKnownLocation(
            LocationManager.NETWORK_PROVIDER
        )*/
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        lManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            1000.toLong(), 1.toFloat(),
            object : LocationListener {
                override fun onLocationChanged(l: Location) {
                    pd.dismiss()
                    var lati = l.latitude
                    var longi = l.longitude
                    // Add a marker in Sydney and move the camera


                    //Toast.makeText(this@MapsActivity, "" + lati + longi, Toast.LENGTH_LONG).show()

                    var g = Geocoder(this@MyAddressActivity, Locale.getDefault())
                    var a=g.getFromLocation(l.latitude,l.longitude,2)
                    var address = a.get(0)
                    var i = Intent(this@MyAddressActivity, AddAddressActivity::class.java)
                    i.putExtra("lati", lati.toString())
                    i.putExtra("longi", longi.toString())
                    //i.putExtra("activity","MyAddressActivity")
                    Toast.makeText(this@MyAddressActivity,address.getAddressLine(0)+"\n"+address.locality,Toast.LENGTH_LONG).show()
                    startActivity(i)
                    finish()

                    overridePendingTransition(
                        R.anim.slide_in_right,
                       R.anim.slide_out_left);


                    lManager.removeUpdates(this)

                }

                override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                }

                override fun onProviderEnabled(provider: String) {
                }

                override fun onProviderDisabled(provider: String) {
                }

            })
    }
    //end
}