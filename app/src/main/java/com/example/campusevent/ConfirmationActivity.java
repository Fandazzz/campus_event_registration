package com.example.campusevent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {
    private TextView txtName, txtRegNo, txtEmail, txtPhone, txtProgramme;
    private TextView txtEvent, txtStatus;
    private Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        txtName = findViewById(R.id.txtName);
        txtRegNo = findViewById(R.id.txtRegNo);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtProgramme = findViewById(R.id.txtProgramme);
        txtEvent = findViewById(R.id.txtEvent);
        txtStatus = findViewById(R.id.txtStatus);
        btnHome = findViewById(R.id.btnHome);

        SharedPreferences preferences = getSharedPreferences("registration_preferences", MODE_PRIVATE);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String regNo = intent.getStringExtra("reg_no");
        String eventName = intent.getStringExtra("event_name");

        // Use the persisted values if this activity is recreated without intent extras.
        name = name != null ? name : preferences.getString("name", "");
        regNo = regNo != null ? regNo : preferences.getString("reg_no", "");
        eventName = eventName != null ? eventName
                : preferences.getString("event_name", getString(R.string.event_name));

        txtEvent.setText(eventName);
        txtName.setText(getString(R.string.label_name, name));
        txtRegNo.setText(getString(R.string.label_registration_number, regNo));
        txtEmail.setText(getString(R.string.label_email, valueOrNotProvided(intent.getStringExtra("email"))));
        txtPhone.setText(getString(R.string.label_phone, valueOrNotProvided(intent.getStringExtra("phone"))));
        txtProgramme.setText(getString(R.string.label_programme, valueOrNotProvided(intent.getStringExtra("programme"))));
        txtStatus.setText(R.string.registration_successful);

        btnHome.setOnClickListener(view -> {
            Intent homeIntent = new Intent(ConfirmationActivity.this, MainActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        });
    }

    private String valueOrNotProvided(String value) {
        return value == null || value.isEmpty() ? getString(R.string.not_provided) : value;
    }
}
