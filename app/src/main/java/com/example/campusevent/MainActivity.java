package com.example.campusevent;

import android.content.Intent;
import android.content.ContentUris;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    private Button btnRegister;
    private LinearLayout registrationsContainer;
    private TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class)));
        registrationsContainer = findViewById(R.id.registrationsContainer);
        txtEmpty = findViewById(R.id.txtEmpty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRegistrations();
    }

    private void displayRegistrations() {
        registrationsContainer.removeAllViews();
        boolean hasRegistrations = false;
        try (Cursor cursor = getContentResolver().query(RegistrationContentProvider.CONTENT_URI,
                null, null, null, null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    hasRegistrations = true;
                    registrationsContainer.addView(createRegistrationCard(new StudentRegistration(
                            cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("name")),
                            cursor.getString(cursor.getColumnIndexOrThrow("registration_number")),
                            cursor.getString(cursor.getColumnIndexOrThrow("email")),
                            cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                            cursor.getString(cursor.getColumnIndexOrThrow("programme")),
                            cursor.getString(cursor.getColumnIndexOrThrow("event_name")))));
                }
            }
        }
        txtEmpty.setVisibility(hasRegistrations ? View.GONE : View.VISIBLE);
    }

    private View createRegistrationCard(StudentRegistration registration) {
        MaterialCardView card = new MaterialCardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);
        card.setRadius(dp(24));
        card.setCardElevation(0);
        card.setStrokeWidth(dp(1));
        card.setStrokeColor(getColor(com.google.android.material.R.color.material_dynamic_neutral90));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(20), dp(20), dp(16));
        card.addView(content);
        content.addView(textView(registration.name, 18, true));
        content.addView(textView(getString(R.string.registration_summary,
                registration.registrationNumber, registration.programme), 14, false));
        content.addView(textView(getString(R.string.contact_summary,
                registration.email, registration.phone), 14, false));

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(android.view.Gravity.END);
        actions.setPadding(0, dp(12), 0, 0);
        MaterialButton edit = new MaterialButton(this, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle);
        edit.setText(R.string.edit);
        edit.setOnClickListener(view -> editRegistration(registration));
        MaterialButton delete = new MaterialButton(this, null,
                com.google.android.material.R.attr.materialButtonOutlinedStyle);
        delete.setText(R.string.delete);
        delete.setOnClickListener(view -> confirmDelete(registration));
        actions.addView(edit);
        actions.addView(delete);
        content.addView(actions);
        return card;
    }

    private TextView textView(String text, int textSize, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(textSize);
        view.setPadding(0, bold ? 0 : dp(8), 0, 0);
        if (bold) view.setTypeface(null, android.graphics.Typeface.BOLD);
        return view;
    }

    private void editRegistration(StudentRegistration registration) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.putExtra("registration_id", registration.id);
        intent.putExtra("name", registration.name);
        intent.putExtra("reg_no", registration.registrationNumber);
        intent.putExtra("email", registration.email);
        intent.putExtra("phone", registration.phone);
        intent.putExtra("programme", registration.programme);
        startActivity(intent);
    }

    private void confirmDelete(StudentRegistration registration) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_registration)
                .setMessage(getString(R.string.delete_confirmation, registration.name))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    getContentResolver().delete(ContentUris.withAppendedId(
                            RegistrationContentProvider.CONTENT_URI, registration.id), null, null);
                    displayRegistrations();
                    Toast.makeText(this, R.string.registration_deleted, Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
