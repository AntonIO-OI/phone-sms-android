package com.anton.phoneandsms;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class PhoneActivity extends AppCompatActivity {

    private EditText etPhoneNumber;
    private TextView tvPhoneInfo;
    private TelephonyManager telephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        initializeViews();
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        displayPhoneInfo();
    }

    private void initializeViews() {
        etPhoneNumber = findViewById(R.id.et_phone_number);
        tvPhoneInfo = findViewById(R.id.tv_phone_info);
        Button btnCall = findViewById(R.id.btn_call);
        Button btnDialer = findViewById(R.id.btn_dialer);

        btnCall.setOnClickListener(v -> makeCall());

        btnDialer.setOnClickListener(v -> openDialer());
    }

    private void makeCall() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permission to make calls", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        
        try {
            startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Error making call: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openDialer() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        if (!phoneNumber.isEmpty()) {
            dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        }
        
        try {
            startActivity(dialIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening dialer: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPhoneInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== PHONE INFORMATION ===\n\n");

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                    == PackageManager.PERMISSION_GRANTED) {
                
                String networkOperatorName = telephonyManager.getNetworkOperatorName();
                String simOperatorName = telephonyManager.getSimOperatorName();
                String networkOperator = telephonyManager.getNetworkOperator();
                String simOperator = telephonyManager.getSimOperator();
                int phoneType = telephonyManager.getPhoneType();
                int networkType = telephonyManager.getNetworkType();
                int simState = telephonyManager.getSimState();
                String countryIso = telephonyManager.getNetworkCountryIso();
                String simCountryIso = telephonyManager.getSimCountryIso();

                info.append("📱 GENERAL INFORMATION:\n");
                info.append("Network Operator: ").append(networkOperatorName != null && !networkOperatorName.isEmpty() ? networkOperatorName : "Not available").append("\n");
                info.append("SIM Operator: ").append(simOperatorName != null && !simOperatorName.isEmpty() ? simOperatorName : "Not available").append("\n");
                info.append("Network Code: ").append(networkOperator != null && !networkOperator.isEmpty() ? networkOperator : "Not available").append("\n");
                info.append("SIM Code: ").append(simOperator != null && !simOperator.isEmpty() ? simOperator : "Not available").append("\n");
                info.append("Network Country: ").append(countryIso != null && !countryIso.isEmpty() ? countryIso.toUpperCase() : "Not available").append("\n");
                info.append("SIM Country: ").append(simCountryIso != null && !simCountryIso.isEmpty() ? simCountryIso.toUpperCase() : "Not available").append("\n\n");
                
                info.append("📞 PHONE TYPE:\n");
                String phoneTypeStr;
                switch (phoneType) {
                    case TelephonyManager.PHONE_TYPE_GSM:
                        phoneTypeStr = "GSM";
                        break;
                    case TelephonyManager.PHONE_TYPE_CDMA:
                        phoneTypeStr = "CDMA";
                        break;
                    case TelephonyManager.PHONE_TYPE_SIP:
                        phoneTypeStr = "SIP";
                        break;
                    default:
                        phoneTypeStr = "Unknown";
                        break;
                }
                info.append("Type: ").append(phoneTypeStr).append("\n\n");

                info.append("📶 NETWORK:\n");
                String networkTypeStr = getNetworkTypeString(networkType);
                info.append("Network Type: ").append(networkTypeStr).append("\n\n");

                info.append("📋 SIM CARD:\n");
                String simStateStr = getSimStateString(simState);
                info.append("SIM State: ").append(simStateStr).append("\n\n");

                info.append("ℹ️ NOTE:\n");
                info.append("Some device identifiers are\n");
                info.append("unavailable for security reasons\n");
                info.append("in Android 10+ (API 29+)\n\n");

                info.append("📱 DEVICE INFORMATION:\n");
                info.append("Model: ").append(Build.MODEL).append("\n");
                info.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
                info.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
                info.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");

            } else {
                info.append("❌ No permission to read phone state\n\n");
                info.append("To get phone information,\n");
                info.append("READ_PHONE_STATE permission is required\n\n");

                info.append("📱 BASIC DEVICE INFORMATION:\n");
                info.append("Model: ").append(Build.MODEL).append("\n");
                info.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
                info.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
                info.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
            }
        } catch (SecurityException e) {
            info.append("🔒 SECURITY RESTRICTIONS:\n");
            info.append("Access to some data is restricted\n");
            info.append("by Android for privacy reasons.\n\n");
            info.append("This is normal behavior for\n");
            info.append("Android 10+ (API 29+)\n\n");

            info.append("📱 AVAILABLE DEVICE INFORMATION:\n");
            info.append("Model: ").append(Build.MODEL).append("\n");
            info.append("Manufacturer: ").append(Build.MANUFACTURER).append("\n");
            info.append("Android Version: ").append(Build.VERSION.RELEASE).append("\n");
            info.append("API Level: ").append(Build.VERSION.SDK_INT).append("\n");
        } catch (Exception e) {
            info.append("❌ ERROR:\n");
            info.append(e.getMessage());
        }

        tvPhoneInfo.setText(info.toString());
    }

    private String getNetworkTypeString(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G NR";
            default:
                return "Unknown (" + networkType + ")";
        }
    }

    private String getSimStateString(int simState) {
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                return "Absent";
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                return "PIN Required";
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                return "PUK Required";
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                return "Network Locked";
            case TelephonyManager.SIM_STATE_READY:
                return "Ready";
            case TelephonyManager.SIM_STATE_NOT_READY:
                return "Not Ready";
            case TelephonyManager.SIM_STATE_PERM_DISABLED:
                return "Permanently Disabled";
            case TelephonyManager.SIM_STATE_CARD_IO_ERROR:
                return "Card I/O Error";
            case TelephonyManager.SIM_STATE_CARD_RESTRICTED:
                return "Restricted";
            default:
                return "Unknown State (" + simState + ")";
        }
    }
} 