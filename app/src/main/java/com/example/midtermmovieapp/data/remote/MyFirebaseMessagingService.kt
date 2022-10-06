package com.example.midtermmovieapp.data.remote

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.midtermmovieapp.MainActivity
import com.example.midtermmovieapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



const val channelId = "notification_channel"
const val channelName = "com.example.midtermmovieapp"


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification != null) {
            generateNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)

        }

    }
    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(title: String, message:String):RemoteViews {
        val remoteView = RemoteViews("com.example.midtermmovieapp",R.layout.push_notification)
        remoteView.setTextViewText(R.id.notificationAppName,title)
        remoteView.setTextViewText(R.id.notificationDesc,message)
        remoteView.setImageViewResource(R.id.ivFavLogo,R.drawable.ic_fav_logo)
        return remoteView

    }
    fun generateNotification(title:String, message:String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        var builder:NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
            channelId)
            .setSmallIcon(R.drawable.ic_baseline_email_24)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title,message))

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)



        }


        notificationManager.notify(0,builder.build())
    }




}