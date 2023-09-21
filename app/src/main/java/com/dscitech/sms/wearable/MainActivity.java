package com.dscitech.sms.wearable;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.WatchViewStub;
import android.telephony.SmsMessage;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;
    private BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final TextView smsTextView = findViewById(R.id.smsTextView);
        //mTextView = (TextView) stub.findViewById(R.id.smsTextView);
        /*stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.smsTextView);
            }
        });*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.RECEIVE_SMS
            }, 1);
        }
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
                            smsTextView.setText(String.format("Sender: %s\nMessage: %s", sender, messageBody));
                        }
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
            } else {
                // Permission denied.
            }
        }
    }
}
