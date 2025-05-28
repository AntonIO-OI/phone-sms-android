package com.anton.phoneandsms;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SmsListActivity extends AppCompatActivity {
    private ArrayList<String> smsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_list);

        initializeViews();
        loadSmsMessages();
    }

    private void initializeViews() {
        ListView listView = findViewById(R.id.lv_sms_list);
        smsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
        listView.setAdapter(adapter);
    }

    private void loadSmsMessages() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permission to read SMS", Toast.LENGTH_SHORT).show();
            return;
        }

        smsList.clear();

        try {
            loadSmsFromUri(Uri.parse("content://sms/inbox"), "📨 Inbox");
            
            loadSmsFromUri(Uri.parse("content://sms/sent"), "📤 Sent");
            
            loadSmsFromUri(Uri.parse("content://sms/draft"), "📝 Drafts");

            if (smsList.isEmpty()) {
                smsList.add("No SMS messages found");
            }

        } catch (Exception e) {
            Toast.makeText(this, "Error reading SMS: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            smsList.add("Error loading SMS");
        }

        adapter.notifyDataSetChanged();
    }

    private void loadSmsFromUri(Uri uri, String typeLabel) {
        try {
            Cursor cursor = getContentResolver().query(
                    uri,
                    new String[]{"address", "body", "date", "type"},
                    null,
                    null,
                    "date DESC LIMIT 50"
            );

            if (cursor != null && cursor.moveToFirst()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                do {
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                    long dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow("date"));
                    
                    String date = dateFormat.format(new Date(dateMillis));
                    
                    String shortBody = body;
                    if (body != null && body.length() > 100) {
                        shortBody = body.substring(0, 100) + "...";
                    }

                    String smsInfo = String.format("%s\nFrom: %s\nDate: %s\nText: %s\n",
                            typeLabel, address, date, shortBody);

                    smsList.add(smsInfo);

                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (Exception e) {
            smsList.add("Error reading " + typeLabel + ": " + e.getMessage());
        }
    }
} 