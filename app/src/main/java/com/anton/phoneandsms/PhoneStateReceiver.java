package com.anton.phoneandsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class PhoneStateReceiver extends BroadcastReceiver {
    private static String lastState = TelephonyManager.EXTRA_STATE_IDLE;
    private static String incomingNumber = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            
            if (number != null && !number.isEmpty()) {
                incomingNumber = number;
            }
            
            if (state != null) {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    if (!lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        String message = "📞 Incoming call";
                        if (!incomingNumber.isEmpty()) {
                            message += "\nFrom: " + incomingNumber;
                        }
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        String message = "✅ Call accepted";
                        if (!incomingNumber.isEmpty()) {
                            message += "\nFrom: " + incomingNumber;
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else if (lastState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                        Toast.makeText(context, "📱 Outgoing call in progress", Toast.LENGTH_SHORT).show();
                    }
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                        String message = "❌ Call missed";
                        if (!incomingNumber.isEmpty()) {
                            message += "\nFrom: " + incomingNumber;
                        }
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else if (lastState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                        Toast.makeText(context, "📴 Call ended", Toast.LENGTH_SHORT).show();
                    }
                    
                    incomingNumber = "";
                }
                
                lastState = state;
            }
        }
    }
} 