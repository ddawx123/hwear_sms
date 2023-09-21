package com.dscitech.sms.wearable.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsMessage;

import com.dscitech.sms.wearable.R;

public class SmsService extends Service {

    private BroadcastReceiver smsReceiver;
    private static final String CHANNEL_ID = "sms_channel_id";

    public SmsService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        final SmsService that = this;
        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(that.getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher).setContentTitle("hi").setContentText("welcome").setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(that);
        notificationManagerCompat.notify(1, builder.build());
        startForeground(1, builder.build());*/
        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus != null) {
                        for (Object pdu : pdus) {
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                            String sender = smsMessage.getOriginatingAddress();
                            String messageBody = smsMessage.getMessageBody();
                            System.out.println(String.format("Sender: %s\nMessage: %s", sender, messageBody));
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(that.getApplicationContext(), CHANNEL_ID).setSmallIcon(R.drawable.ic_launcher).setContentTitle(sender).setContentText(messageBody).setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(that);
                            notificationManagerCompat.notify(1, builder.build());
                            startForeground(1, builder.build());
                            Intent subIntent = new Intent("my_sms_action");
                            subIntent.putExtra("sms_message", String.format("Sender: %s\nMessage: %s", sender, messageBody));
                            sendBroadcast(subIntent);
                        }
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    /**
     * 创建通知频道（高版本系统专用）
     * @return void
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SMS Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
