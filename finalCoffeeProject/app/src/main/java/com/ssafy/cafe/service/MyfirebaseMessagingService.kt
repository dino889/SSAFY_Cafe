package com.ssafy.cafe.service

import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.cafe.R
import com.ssafy.cafe.activity.MainActivity
import com.ssafy.cafe.config.ApplicationClass

private const val TAG = "MyFirebaseMsgSvc_싸피"

class MyFirebaseMessageService : FirebaseMessagingService() {
    // 새로운 토큰이 생성될 때 마다 해당 콜백이 호출된다.
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
        // 새로운 토큰 수신 시 서버로 전송
        MainActivity.uploadToken(token, ApplicationClass.sharedPreferencesUtil.getUser().id)
    }

    // Foreground에서 Push Service를 받기 위해 Notification 설정
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "onMessageReceived: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            val messageTitle = remoteMessage.data.getValue("title")
            val messageContent = remoteMessage.data.getValue("body")

            val mainIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            val mainPendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, mainIntent, 0)

//            val style: Notification.BigTextStyle = Notification.BigTextStyle(mBuilder)
//            style.setSummaryText("and More +")
//            style.setBigContentTitle("BigText Expanded Title")
//            style.bigText(
//                "Mir's IT Blog adress is \"itmir.tistory.com\"," +
//                        "Welcome to the Mir's Blog!! Nice to Meet you, this is Example JellyBean Notification"
//            )
//
//            mBuilder.setStyle(style)

            val builder1 = NotificationCompat.Builder(this, MainActivity.channel_id)
                .setSmallIcon(R.drawable.notification_alert_bell_icon)
                .setContentTitle(messageTitle)
                .setContentText(messageContent)
                .setStyle(NotificationCompat.BigTextStyle().bigText(messageContent))
                .setAutoCancel(true)
                .setColor(Color.blue(700))
                .setContentIntent(mainPendingIntent)

            NotificationManagerCompat.from(this).apply {
                notify(101, builder1.build())
            }

            val intent = Intent("com.ssafy.cafe")
            sendBroadcast(intent)
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
    }


}