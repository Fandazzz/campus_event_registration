package com.example.campusevent;

import android.content.Intent;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {
    private EditText edtName, edtRegNo, edtEmail, edtPhone, edtProgramme;
    private Button btnSubmit;
    private long registrationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        edtName = findViewById(R.id.edtName);
        edtRegNo = findViewById(R.id.edtRegNo);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtProgramme = findViewById(R.id.edtProgramme);
        btnSubmit = findViewById(R.id.btnSubmit);
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());

        registrationId = getIntent().getLongExtra("registration_id", -1);
        if (registrationId != -1) {
            populateForEdit();
            btnSubmit.setText(R.string.update_registration);
        }

        btnSubmit.setOnClickListener(view -> submitRegistration());
    }

    private void submitRegistration() {
        String name = edtName.getText().toString().trim();
        String regNo = edtRegNo.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String programme = edtProgramme.getText().toString().trim();

        if (!validateInput(name, regNo, email, phone)) {
            return;
        }
        if (programme.isEmpty()) {
            edtProgramme.setError("Programme is required");
            edtProgramme.requestFocus();
            return;
        }

        String eventName = getString(R.string.event_name);
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("registration_number", regNo);
        values.put("email", email);
        values.put("phone", phone);
        values.put("programme", programme);
        values.put("event_name", eventName);
        boolean saved = registrationId == -1
                ? getContentResolver().insert(RegistrationContentProvider.CONTENT_URI, values) != null
                : getContentResolver().update(
                        android.content.ContentUris.withAppendedId(
                                RegistrationContentProvider.CONTENT_URI, registrationId),
                        values, null, null) > 0;
        if (!saved) {
            btnSubmit.setError(getString(R.string.save_failed));
            return;
        }
        SharedPreferences preferences = getSharedPreferences("registration_preferences", MODE_PRIVATE);
        preferences.edit()
                .putString("name", name)
                .putString("reg_no", regNo)
                .putString("event_name", eventName)
                .apply();

        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("reg_no", regNo);
        intent.putExtra("email", email);
        intent.putExtra("phone", phone);
        intent.putExtra("programme", programme);
        intent.putExtra("event_name", eventName);
        startActivity(intent);
    }

    private void populateForEdit() {
        edtName.setText(getIntent().getStringExtra("name"));
        edtRegNo.setText(getIntent().getStringExtra("reg_no"));
        edtEmail.setText(getIntent().getStringExtra("email"));
        edtPhone.setText(getIntent().getStringExtra("phone"));
        edtProgramme.setText(getIntent().getStringExtra("programme"));
    }

    private boolean validateInput(String name, String regNo,
                                  String email, String phone) {
        if (name.isEmpty()) {
            edtName.setError("Name is required");
            edtName.requestFocus();
            return false;
        }
        if (regNo.isEmpty()) {
            edtRegNo.setError("Registration number is required");
            edtRegNo.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email address");
            edtEmail.requestFocus();
            return false;
        }
        if (!phone.matches("^[0-9+()\\-\\s]{7,20}$")) {
            edtPhone.setError("Enter a valid phone number");
            edtPhone.requestFocus();
            return false;
        }
        return true;
    }
}
