package com.a2zdukhana.store.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.a2zdukhana.store.ItemsActivity
import com.a2zdukhana.store.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.URL

class MessageService : FirebaseMessagingService() {

    var x=0
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(p0: RemoteMessage?) {
        super.onMessageReceived(p0)
            //sendNotification(p0!!.data.get("message"),p0!!.data.get("title"),p0!!.data.get("img"))
        if (p0 != null) {
            sendNotification(p0.notification!!.body,p0.notification!!.title,p0.data.get("img"),p0.data.get("category"),p0.data.get("subcategory"))
        }

    }

    private fun sendNotification(body: String?, title: String?,img:String?,cat:String?,subcat:String?) {
            var i = Intent(this,ItemsActivity::class.java)
        var url = URL(img)
        var channel_id = "default"
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.putExtra("cato",cat)
            i.putExtra("subcato",subcat)
            var p = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_ONE_SHOT)
        var bigpic = NotificationCompat.BigPictureStyle()
        bigpic.bigPicture(BitmapFactory.decodeStream(url.openConnection().getInputStream()))
        var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var nm=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var nf= NotificationCompat.Builder(this,channel_id)
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setContentIntent(p).setSmallIcon(R.mipmap.ic_launcher).setSound(uri)
            .setStyle(bigpic)
        if(x>10737418)
            x=0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var n=  NotificationChannel(channel_id,"default channel",NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(n)
        }
        nm.notify(x++,nf.build())
    }
}