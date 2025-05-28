package com.anton.phoneandsms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class SmsActivity extends AppCompatActivity {
    private EditText etPhoneNumber, etMessage;
    private TextView tvStatus;
    private static final String SENT = "SMS_SENT";
    private static final String DELIVERED = "SMS_DELIVERED";

    private BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case RESULT_OK:
                    updateStatus("SMS sent successfully!");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    updateStatus("Error: Generic failure");
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    updateStatus("Error: No service");
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    updateStatus("Error: Null PDU");
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    updateStatus("Error: Radio off");
                    break;
            }
        }
    };

    private BroadcastReceiver deliveredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()) {
                case RESULT_OK:
                    updateStatus("SMS delivered!");
                    break;
                case RESULT_CANCELED:
                    updateStatus("SMS not delivered");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        initializeViews();
        registerReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(sentReceiver);
            unregisterReceiver(deliveredReceiver);
        } catch (IllegalArgumentException e) {
        }
    }

    private void initializeViews() {
        etPhoneNumber = findViewById(R.id.et_sms_phone_number);
        etMessage = findViewById(R.id.et_sms_message);
        tvStatus = findViewById(R.id.tv_sms_status);
        Button btnSendSms = findViewById(R.id.btn_send_sms);
        Button btnSendLongSms = findViewById(R.id.btn_send_long_sms);

        btnSendSms.setOnClickListener(v -> sendSms());

        btnSendLongSms.setOnClickListener(v -> sendLongSms());
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerReceivers() {
        IntentFilter sentFilter = new IntentFilter(SENT);
        IntentFilter deliveredFilter = new IntentFilter(DELIVERED);
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) { // API 33+
            registerReceiver(sentReceiver, sentFilter, Context.RECEIVER_NOT_EXPORTED);
            registerReceiver(deliveredReceiver, deliveredFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(sentReceiver, sentFilter);
            registerReceiver(deliveredReceiver, deliveredFilter);
        }
    }

    private void sendSms() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permission to send SMS", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, 
                    new Intent(SENT), PendingIntent.FLAG_IMMUTABLE);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, 
                    new Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE);

            smsManager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
            updateStatus("Sending SMS...");

        } catch (Exception e) {
            updateStatus("Error sending SMS: " + e.getMessage());
        }
    }

    private void sendLongSms() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permission to send SMS", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            SmsManager smsManager = SmsManager.getDefault();

            ArrayList<String> parts = smsManager.divideMessage(message);
            
            ArrayList<PendingIntent> sentPIs = new ArrayList<>();
            ArrayList<PendingIntent> deliveredPIs = new ArrayList<>();

            for (int i = 0; i < parts.size(); i++) {
                sentPIs.add(PendingIntent.getBroadcast(this, i, 
                        new Intent(SENT), PendingIntent.FLAG_IMMUTABLE));
                deliveredPIs.add(PendingIntent.getBroadcast(this, i, 
                        new Intent(DELIVERED), PendingIntent.FLAG_IMMUTABLE));
            }

            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentPIs, deliveredPIs);
            updateStatus("Sending long SMS (" + parts.size() + " parts)...");

        } catch (Exception e) {
            updateStatus("Error sending long SMS: " + e.getMessage());
        }
    }

    private void updateStatus(String status) {
        tvStatus.setText(status);
        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
    }
} 