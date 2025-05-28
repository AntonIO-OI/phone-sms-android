package com.anton.phoneandsms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CallLogActivity extends AppCompatActivity {
    private ArrayList<String> callLogList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        initializeViews();
        loadCallLog();
    }

    private void initializeViews() {
        ListView listView = findViewById(R.id.lv_call_log);
        callLogList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, callLogList);
        listView.setAdapter(adapter);
    }

    private void loadCallLog() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permission to read call log", Toast.LENGTH_SHORT).show();
            return;
        }

        callLogList.clear();

        try {
            Cursor cursor = getContentResolver().query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    null,
                    null,
                    CallLog.Calls.DATE + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                do {
                    String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
                    long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));

                    String date = dateFormat.format(new Date(dateMillis));

                    String callType;
                    switch (type) {
                        case CallLog.Calls.INCOMING_TYPE:
                            callType = "📞 Incoming";
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            callType = "📱 Outgoing";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            callType = "❌ Missed";
                            break;
                        case CallLog.Calls.REJECTED_TYPE:
                            callType = "🚫 Rejected";
                            break;
                        case CallLog.Calls.BLOCKED_TYPE:
                            callType = "🔒 Blocked";
                            break;
                        default:
                            callType = "❓ Unknown";
                            break;
                    }

                    String durationStr = formatDuration(duration);

                    String displayName = (name != null && !name.isEmpty()) ? name : "Unknown";
                    String callInfo = String.format("%s\n%s (%s)\n%s\nDuration: %s\n",
                            callType, displayName, number, date, durationStr);

                    callLogList.add(callInfo);

                } while (cursor.moveToNext());

                cursor.close();
            }

            if (callLogList.isEmpty()) {
                callLogList.add("Call log is empty");
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error reading call log: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            callLogList.add("Error loading call log");
        }

        adapter.notifyDataSetChanged();
    }

    private String formatDuration(long seconds) {
        if (seconds == 0) {
            return "0 sec";
        }

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d h %d m %d s", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(), "%d m %d s", minutes, secs);
        } else {
            return String.format(Locale.getDefault(), "%d s", secs);
        }
    }
} 