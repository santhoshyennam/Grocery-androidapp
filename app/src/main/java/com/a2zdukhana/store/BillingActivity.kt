package com.a2zdukhana.store

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.a2zdukhana.store.classes.ItemClass1
import android.annotation.SuppressLint
import com.a2zdukhana.store.classes.BillingClass
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_billing.*
import kotlinx.android.synthetic.main.activity_billing.back
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.random.Random


@Suppress("DEPRECATION")
class BillingActivity : AppCompatActivity() {
    val UPI_PAYMENT = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing)
        //Toast.makeText(this,intent.getStringExtra("price"),Toast.LENGTH_LONG).show()
        var db = FirebaseDatabase.getInstance().getReference("gateway")
        back.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);


        }
        upi.setOnClickListener {
            db.child("upi").addValueEventListener(
                object : ValueEventListener
                {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                            var x = p0.value.toString()
                            if(x.equals("1"))
                            {
                                payUsingUpi()
                            }
                            else{
                                Toast.makeText(this@BillingActivity,"Sorry,We are currently not accepting UPI Payments",Toast.LENGTH_LONG).show()
                            }
                    }

                })

        }
        gateway.setOnClickListener {
            db.child("online").addValueEventListener(
                object : ValueEventListener
                {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var x = p0.value.toString()
                        if(x.equals("1"))
                        {
                            //payUsingUpi()
                        }
                        else{
                            Toast.makeText(this@BillingActivity,"Sorry,We are currently not accepting Online Payment",Toast.LENGTH_LONG).show()
                        }
                    }

                })
        }
        cash.setOnClickListener {
            db.child("cod").addValueEventListener(
                object : ValueEventListener
                {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var x = p0.value.toString()
                        if(x.equals("1"))
                        {
                            var alert= AlertDialog.Builder(this@BillingActivity)
                            alert.setMessage("Do you want confirm order?")
                                .setCancelable(false)
                                .setPositiveButton("yes",
                                    DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                                        //var uid = FirebaseAuth.getInstance().uid
                                        var pd = ProgressDialog(this@BillingActivity)
                                        pd.setTitle("please wait..")
                                        pd.show()
                                        storeOrder("COD")
                                        /*var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("cart")
                                        db.addListenerForSingleValueEvent(
                                            object : ValueEventListener
                                            {
                                                override fun onCancelled(p0: DatabaseError) {

                                                }

                                                @RequiresApi(Build.VERSION_CODES.O)
                                                override fun onDataChange(p0: DataSnapshot) {

                                                    /*tb.child(x).child("details").setValue(p0.value)
                                                    tb.child(x).child("uid").setValue(uid.toString())
                                                    tb.child(x).child("datetime").setValue(d.toString())
                                                    tb.child(x).child("amount").setValue(intent.getStringExtra("price"))
                                                    tb.child(x).child("address").setValue(" "+intent.getStringExtra("address"))
                                                    tb.child(x).child("timeslot").setValue(intent.getStringExtra("timeslot"))
                                                    tb.child(x).child("type").setValue("cash")
                                                    tb.child(x).child("status").setValue("pending")
                                                    var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).
                                                    child("orders").child(x)
                                                    dbase.child("details").setValue(p0.value)
                                                    dbase.child("datetime").setValue(d.toString())
                                                    dbase.child("amount").setValue(intent.getStringExtra("price"))
                                                    dbase.child("address").setValue(" "+intent.getStringExtra("address"))
                                                    dbase.child("timeslot").setValue(intent.getStringExtra("timeslot"))
                                                    dbase.child("type").setValue("cash")
                                                    dbase.child("status").setValue("pending")
                                                    db.removeValue()
                                                    var policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                                                    StrictMode.setThreadPolicy(policy)
                                                    //sendemail(uid,intent.getStringExtra("price"),intent.getStringExtra("timeslot"),"cash",d.toString(),intent.getStringExtra("address"))
                                                    Toast.makeText(this@BillingActivity,"order successful",Toast.LENGTH_SHORT).show()
                                                    pd.dismiss()
                                                    startActivity(Intent(this@BillingActivity,OrderPlaced::class.java).putExtra("orderid",x))
                                                    finish()*/
                                                }

                                            }
                                        )*/
                                    })
                                .setNegativeButton("no",DialogInterface.OnClickListener(){ dialogInterface: DialogInterface, i: Int ->
                                    dialogInterface.cancel()
                                })
                            var al = alert.create()
                            al.show()
                        }
                        else
                        {
                            Toast.makeText(this@BillingActivity,"Sorry,We are currently not accepting cash on delivery",Toast.LENGTH_LONG).show()
                        }
                    }

                }
            )

        }
    }



    private fun payUsingUpi() {
        var db = FirebaseDatabase.getInstance().getReference("myinformation").child("upi")
        db.addListenerForSingleValueEvent(
            object : ValueEventListener
            {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists())
                    {
                        val uri: Uri = Uri.parse("upi://pay").buildUpon()
                            .appendQueryParameter("pa", p0.value.toString())
                            .appendQueryParameter("pn", "srinivas")
                            .appendQueryParameter("tn", "for A2ZDukhana")
                            .appendQueryParameter("am", intent.getStringExtra("price"))
                            .appendQueryParameter("cu", "INR")
                            .build()


                        val upiPayIntent = Intent(Intent.ACTION_VIEW)
                        upiPayIntent.data = uri

                        // will always show a dialog to user to choose an app

                        // will always show a dialog to user to choose an app
                        val chooser = Intent.createChooser(upiPayIntent, "Pay with")

                        // check if intent resolves

                        // check if intent resolves
                        if (null != chooser.resolveActivity(packageManager)) {
                            startActivityForResult(chooser, UPI_PAYMENT)
                        } else {
                            Toast.makeText(
                                this@BillingActivity,
                                "No UPI app found, please install one to continue",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(
                            this@BillingActivity,
                            "Sorry,We are currently not accepting UPI Payments",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        )


    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UPI_PAYMENT -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    //Log.d("UPI", "onActivityResult: $trxt")
                    val dataList: ArrayList<String> = ArrayList()
                    dataList.add(trxt!!)
                    upiPaymentDataOperation(dataList)
                } else {
                    //Log.d("UPI", "onActivityResult: " + "Return data is null")
                    val dataList: ArrayList<String> = ArrayList()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
               /* Log.d(
                    "UPI",
                    "onActivityResult: " + "Return data is null"
                ) //when user simply back without payment*/
                val dataList: ArrayList<String> = ArrayList()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isConnectionAvailable(this@BillingActivity)) {
            // Toast.makeText(this,data[0],Toast.LENGTH_SHORT).show()
            var str = data[0]
            //Log.d("UPIPAY", "upiPaymentDataOperation: $str")
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).toTypedArray()
            for (i in response.indices) {
                val equalStr =
                    response[i].split("=".toRegex()).toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                        status = equalStr[1].toLowerCase()
                    } else if (equalStr[0]
                            .toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0]
                            .toLowerCase() == "txnRef".toLowerCase()
                    ) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }
            if (status == "success") {
                //Code to handle successful transaction here.
                storeOrder("upi")

                /*var uid = FirebaseAuth.getInstance().uid
                var db = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("cart")
                db.addListenerForSingleValueEvent(
                    object : ValueEventListener
                    {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onDataChange(p0: DataSnapshot) {
                            /*var d1 = DateTimeFormatter.ofPattern("MMM dd yyyy,HH:mm")
                            var d= LocalDateTime.now().format(d1)
                            tb.child(x).child("details").setValue(p0.value)
                            tb.child(x).child("uid").setValue(uid.toString())
                            tb.child(x).child("datetime").setValue(d.toString())
                            tb.child(x).child("amount").setValue(intent.getStringExtra("price"))
                            tb.child(x).child("address").setValue(" "+intent.getStringExtra("address"))
                            tb.child(x).child("timeslot").setValue(intent.getStringExtra("timeslot"))
                            tb.child(x).child("type").setValue("upi")
                            tb.child(x).child("status").setValue("pending")
                            var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).
                            child("orders").child(x)
                            dbase.child("details").setValue(p0.value)
                            dbase.child("datetime").setValue(d.toString())
                            dbase.child("amount").setValue(intent.getStringExtra("price"))
                            dbase.child("address").setValue(" "+intent.getStringExtra("address"))
                            dbase.child("timeslot").setValue(intent.getStringExtra("timeslot"))
                            dbase.child("type").setValue("upi")
                            dbase.child("status").setValue("pending")
                            db.removeValue()
                            Toast.makeText(this@BillingActivity,"order successful",Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@BillingActivity,OrderPlaced::class.java).putExtra("orderid",x))
                            finish()*/
                        }

                    }
                )*/
                Toast.makeText(this@BillingActivity, "Transaction successful.", Toast.LENGTH_SHORT)
                    .show()
                //Log.d("UPI", "responseStr: $approvalRefNo")
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this@BillingActivity, "Payment cancelled by user.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@BillingActivity,
                    "Transaction failed.Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this@BillingActivity,
                "Internet connection is not available. Please check and try again",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("MissingPermission")
    fun isConnectionAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val netInfo = connectivityManager.activeNetworkInfo
            if (netInfo != null && netInfo.isConnected
                && netInfo.isConnectedOrConnecting
                && netInfo.isAvailable
            ) {
                return true
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    override fun onStart() {
        super.onStart()

        var user  = FirebaseAuth.getInstance().currentUser
        if(user==null)
        {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }


            var cm=getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if(cm.activeNetworkInfo==null)
            {
                startActivity(Intent(this,NetConnection::class.java))
            }


    }

    private fun storeOrder(s: String)
    {
        var uid = FirebaseAuth.getInstance().uid
        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString()).child("cart")

        dbase.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        var lis = mutableListOf<ItemClass1>()
                        var chi = p0.children
                        var last = p0.children.last().key.toString()
                        var tb = FirebaseDatabase.getInstance().getReference("orders")
                        val max=999
                        val min=100
                        val random = Random.nextInt((max - min) + 1) + min
                        var c= Date()
                        var d =Calendar.getInstance()
                        d.time=c
                        var a= arrayOf(d.get(Calendar.YEAR).toString()
                            ,(d.get(Calendar.MONTH)+1).toString(),d.get(Calendar.DATE).toString(),d.get(Calendar.HOUR_OF_DAY).toString(),d.get(Calendar.MINUTE).toString()
                            ,d.get(Calendar.SECOND).toString())
                        for(i in 0..a.size-1)
                        {
                            if(a[i].length==1)
                                a[i]="0"+a[i]
                        }
                        var tbkey = "ORD"+a[0]+a[1]+a[2]+a[3]+a[4]+a[5]+random.toString()
                        var dbase = FirebaseDatabase.getInstance().getReference("usersinformation").child(uid.toString())
                        chi.forEach {

                            var x = it.key.toString().split(":")
                            var db = FirebaseDatabase.getInstance().getReference("categories")
                                .child(x[0]).child("subcategory")
                                .child(x[1]).child(x[2])
                            var q = (x[3].toInt()+1).toString()
                            db.addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                    @RequiresApi(Build.VERSION_CODES.O)
                                    override fun onDataChange(p1: DataSnapshot) {
                                        var chi1 = p1.children
                                        var map = HashMap<String, String>()


                                        chi1.forEach {
                                            when (it.key) {
                                                "quantity" -> {
                                                    var chi2 = it.children
                                                    chi2.forEach {
                                                        when(it.key){
                                                            q->{
                                                                var chi3 =it.children
                                                                chi3.forEach {
                                                                    map.put(it.key.toString(),it.value.toString())
                                                                }
                                                            }
                                                        }
                                                    }

                                                }
                                                else -> {
                                                    map.put(it.key.toString(), it.value.toString())
                                                }
                                            }

                                        }
                                        //Toast.makeText(this@CartActivity,x[3],Toast.LENGTH_LONG).show()
                                        var i = ItemClass1(
                                            map.getValue("name"),
                                            map.getValue("image"),
                                            map.getValue("cost"),
                                            map.getValue("desc"),
                                            map.getValue("discount"),
                                            x[0],
                                            x[1],
                                            it.value.toString(),
                                            map.getValue("quantity"),it.key.toString()
                                        )
                                        lis.add(i)
                                        var x = dbase.push().key.toString()
                                        tb.child(tbkey).child("details").child(x).setValue(i)
                                        dbase.child("orders").child(tbkey).child("details").child(x).setValue(i)
                                        if (lis.size != 0 && it.key.toString().equals(last)) {
                                            //start

                                            var db =FirebaseDatabase.getInstance().getReference("myinformation").child("orderto")
                                            db.addListenerForSingleValueEvent(
                                                object : ValueEventListener {
                                                    override fun onCancelled(p0: DatabaseError) {

                                                    }

                                                    override fun onDataChange(p0: DataSnapshot) {
                                                        var num = p0.value.toString()
                                                        var d1 = DateTimeFormatter.ofPattern("MMM dd yyyy,HH:mm")
                                                        var d= LocalDateTime.now().format(d1)
                                                        var b = BillingClass(uid.toString(),d.toString(),intent.getStringExtra("price")!!,intent.getStringExtra("address")!!
                                                        ,intent.getStringExtra("timeslot")!!,s,"pending",intent.getStringExtra("deliverycharges")!!,lis)
                                                        tb.child(tbkey).setValue(b).addOnCompleteListener(
                                                            object : OnCompleteListener<Void> {
                                                                override fun onComplete(p0: Task<Void>) {
                                                                    dbase.child("orders").child(tbkey).setValue(b)
                                                                        .addOnCompleteListener(
                                                                            object : OnCompleteListener<Void>{
                                                                                override fun onComplete(
                                                                                    p0: Task<Void>
                                                                                ) {


                                                                                    dbase.child("cart")
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(
                                                                                            object :
                                                                                                OnCompleteListener<Void> {
                                                                                                override fun onComplete(
                                                                                                    p0: Task<Void>
                                                                                                ) {
                                                                                                    startActivity(Intent(this@BillingActivity,OrderPlaced::class.java).putExtra("orderid",tbkey)
                                                                                                        .putExtra("mobile",num))
                                                                                                    finish()
                                                                                                }
                                                                                            })



                                                                                }

                                                                            }
                                                                        )
                                                                }

                                                            }
                                                        )

                                                       /* var mt = MyTask(this@BillingActivity, tbkey,num)
                                                        mt.execute()*/
                                                    }
                                                })

                                            //ends




                                        }


                                    }

                                }
                            )

                        }

                    }



                }
            }
        )


    }
}