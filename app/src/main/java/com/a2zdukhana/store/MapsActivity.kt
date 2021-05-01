package com.a2zdukhana.store

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_maps.*


@Suppress("DEPRECATION")
class MapsActivity() : AppCompatActivity(), OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    ResultCallback<LocationSettingsResult> {

    private lateinit var mMap: GoogleMap
    protected var mGoogleApiClient: GoogleApiClient? = null
    protected var locationRequest: LocationRequest? = null
    var REQUEST_CHECK_SETTINGS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);

        }

        if(isLocationEnabled(this))
        {

        }
        if((!intent.getStringExtra("activity")!!.equals("contact") && isLocationEnabled(this) )
            || intent.getStringExtra("activity")!!.equals("contact"))
        {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }
        else
        {
            initializeLocation()
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        var lati= intent.getStringExtra("lati")
        var longi = intent.getStringExtra("longi")
        if(!intent.getStringExtra("activity").equals("contact")) {
            //Toast.makeText(this,"Turn on GPS if not",Toast.LENGTH_LONG).show()
            var lManager: LocationManager = getSystemService(
                Context.LOCATION_SERVICE
            ) as LocationManager
            /*lManager.getLastKnownLocation(
                LocationManager.NETWORK_PROVIDER
            )*/
            var pd = ProgressDialog(this@MapsActivity)
            pd.setTitle("Accessing GPS..")
            pd.show()
            pd.setCancelable(false)
            lManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000.toLong(), 1.toFloat(),
                object : LocationListener {
                    override fun onLocationChanged(l: Location) {
                        var lati = l.latitude
                        var longi = l.longitude
                        pd.dismiss()
                        // Add a marker in Sydney and move the camera
                        val mylocation = LatLng(lati, longi)
                        googleMap.addMarker(
                            MarkerOptions().position(mylocation).title("Your Location")
                        )
                        googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(mylocation, 18.toFloat())
                        )
                        //Toast.makeText(this@MapsActivity, "" + lati + longi, Toast.LENGTH_LONG).show()
                        ok.setOnClickListener {
                            var i = Intent(this@MapsActivity, AddAddressActivity::class.java)
                            i.putExtra("lati", lati.toString())
                            i.putExtra("longi", longi.toString())
                            startActivity(i)
                            finish()

                            overridePendingTransition(R.anim.slide_in_right,
                                R.anim.slide_out_left);

                        }
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
        else
        {
            val mylocation = LatLng(lati!!.toDouble(), longi!!.toDouble())
            googleMap.addMarker(
                MarkerOptions().position(mylocation).title("Your Location")
            )
            googleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(mylocation, 18.toFloat()))
            ok.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()

        var user  = FirebaseAuth.getInstance().currentUser
        if(user==null)
        {
            startActivity(Intent(this,LoginActivity::class.java))
        }
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
                        this@MapsActivity,
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
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)

                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
                finish()
            }

        }
    }

    private fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left,
            R.anim.slide_out_right);

    }

}