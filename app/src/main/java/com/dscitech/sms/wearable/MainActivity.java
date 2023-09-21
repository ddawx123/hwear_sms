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
import android.widget.TextView;
import android.widget.Toast;

import com.dscitech.sms.wearable.Services.SmsService;

public class MainActivity extends Activity {

    private TextView mTextView;
    private BroadcastReceiver smsReceiver;
    private final String[] require_permission_list = {
        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.RECEIVE_SMS
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView smsTextView = findViewById(R.id.smsTextView);
        boolean shouldRequestPerm = false;
        for (String permission : require_permission_list) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                shouldRequestPerm = true;
                break;
            }
        }
        if (shouldRequestPerm) {
            ActivityCompat.requestPermissions(this, require_permission_list, 1);
        } else {
            startService(new Intent(this, SmsService.class));
            smsReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String smsMessage = intent.getStringExtra("sms_message");
                    smsTextView.setText(smsMessage);
                }
            };
            IntentFilter intentFilter = new IntentFilter("my_sms_action");
            registerReceiver(smsReceiver, intentFilter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean preventStartService = false;
        if (requestCode == 1) {
            if (grantResults.length == 0) {
                Toast.makeText(this, "未授权", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        preventStartService = true;
                        Toast.makeText(this, "未授权", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                // 权限核查全部通过的 开始启动服务
                if (!preventStartService) startService(new Intent(this, SmsService.class));
            }
        }
    }
}
