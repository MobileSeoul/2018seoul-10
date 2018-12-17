package com.example.dobitnarae.fcm;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.dobitnarae.LoginActivity;
import com.example.dobitnarae.MainActivity;
import com.example.dobitnarae.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // 메세지가 올때 여기서 처리
        // 앱이 실행중일때 여기서 이벤트를 받습니다.
        //Map<String, String> bundle = remoteMessage.getData();
        sendNotification(remoteMessage.getData().get("num2"), remoteMessage.getData().get("num1"));
    }

    private void sendNotification(String messageTitle, String messageBody) {
        // TODO 어디로 보낼지 수정 필요
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        // 푸시알림 아이콘 및 기타 수정은 여기서
        String channelId = getString(R.string.default_notification_channel_id);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 안드로이드 오레오 버전 이상일때
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(messageTitle) // 이부분은 어플 켜놓은 상태에서 알림 메세지 받으면 저 텍스트로 띄워준다.
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            // Create channel to show notifications.
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.parseColor("#ff8484"));
            channel.setVibrationPattern(new long[]{100, 100});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
        }
        else{
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("오늘하루 한복") // 이부분은 어플 켜놓은 상태에서 알림 메세지 받으면 저 텍스트로 띄워준다.
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{100, 100})
                    .setColor(Color.parseColor("#ff8484"))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }

        // 화면 깨우기
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
        PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
        wakeLock.acquire(3000);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}