package com.example.silentvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.silentvoice.api.ApiService;
import com.example.silentvoice.api.LoginResponse;
import com.example.silentvoice.api.UpdateProfileRequest;
import com.example.silentvoice.api.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ScrollView;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgSelectedProfile;

    TextView btnBack, btnThemeProfile, tvTitle, tvProfileEmail;

    EditText etName, etEmail, etPhone;

    Button btnSave;

    boolean isDarkMode;

    ActivityResultLauncher<String> pickImageLauncher;

    SharedPreferences prefs;

    LinearLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        isDarkMode = ThemeManager.isDarkMode(this);

        prefs = getSharedPreferences("USER_PROFILE", MODE_PRIVATE);

        imgSelectedProfile = findViewById(R.id.imgSelectedProfile);
        btnBack = findViewById(R.id.btnBack);
        btnThemeProfile = findViewById(R.id.btnThemeProfile);
        tvTitle = findViewById(R.id.tvTitle);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        mainLayout = findViewById(R.id.mainLayout);

        Button btnFeedback = findViewById(R.id.btnFeedback);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        btnFeedback.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FeedbackActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logoutUser());

        btnDeleteAccount.setOnClickListener(v -> deleteAccount());

        loadProfile();
        applyTheme();

        pickImageLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.GetContent(),
                        uri -> {
                            if (uri != null) {
                                imgSelectedProfile.setImageURI(uri);
                            }
                        });

        imgSelectedProfile.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        btnThemeProfile.setOnClickListener(v -> {

            isDarkMode = !isDarkMode;

            ThemeManager.saveTheme(
                    ProfileActivity.this,
                    isDarkMode
            );

            applyTheme();
        });

        btnSave.setOnClickListener(v -> saveProfile());

        btnBack.setOnClickListener(v -> finish());
    }

    private void logoutUser() {

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        userPrefs.edit().clear().apply();
        prefs.edit().clear().apply();

        Intent intent =
                new Intent(ProfileActivity.this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    private void deleteAccount() {

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        int userId = userPrefs.getInt("userId", -1);

        if (userId == -1) {

            Toast.makeText(
                    this,
                    "User not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ApiService api =
                RetrofitClientSpring.getClient().create(ApiService.class);

        api.deleteUser(userId)
                .enqueue(new Callback<LoginResponse>() {

                    @Override
                    public void onResponse(
                            Call<LoginResponse> call,
                            Response<LoginResponse> response
                    ) {

                        if (response.isSuccessful()) {

                            userPrefs.edit().clear().apply();
                            prefs.edit().clear().apply();

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Account deleted",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent =
                                    new Intent(
                                            ProfileActivity.this,
                                            LoginActivity.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);

                            finish();

                        } else {

                            try {

                                String error =
                                        response.errorBody() != null
                                                ? response.errorBody().string()
                                                : "No error body";

                                Toast.makeText(
                                        ProfileActivity.this,
                                        "Delete failed: " + response.code() + "\n" + error,
                                        Toast.LENGTH_LONG
                                ).show();

                            } catch (Exception e) {

                                Toast.makeText(
                                        ProfileActivity.this,
                                        "Delete failed: " + response.code(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LoginResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ProfileActivity.this,
                                "Connection Failed: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void loadProfile() {

        String name = prefs.getString("name", "");
        String email = prefs.getString("email", "");
        String phone = prefs.getString("phone", "");

        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);

        tvProfileEmail.setText(email);
    }

    private void saveProfile() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(
                    this,
                    "Name and email are required",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        int userId = userPrefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api =
                RetrofitClientSpring.getClient().create(ApiService.class);

        UpdateProfileRequest request =
                new UpdateProfileRequest(
                        userId,
                        name,
                        email,
                        phone
                );

        api.updateProfile(request)
                .enqueue(new Callback<UserResponse>() {

                    @Override
                    public void onResponse(
                            Call<UserResponse> call,
                            Response<UserResponse> response
                    ) {

                        if (response.isSuccessful() && response.body() != null) {

                            UserResponse updatedUser = response.body();

                            String updatedName = updatedUser.getFullName() != null
                                    ? updatedUser.getFullName()
                                    : name;

                            String updatedEmail = updatedUser.getEmail() != null
                                    ? updatedUser.getEmail()
                                    : email;

                            String updatedPhone = updatedUser.getPhone() != null
                                    ? updatedUser.getPhone()
                                    : phone;

                            prefs.edit()
                                    .putString("name", updatedName)
                                    .putString("email", updatedEmail)
                                    .putString("phone", updatedPhone)
                                    .apply();

                            userPrefs.edit()
                                    .putString("username", updatedName)
                                    .putString("email", updatedEmail)
                                    .apply();

                            etName.setText(updatedName);
                            etEmail.setText(updatedEmail);
                            etPhone.setText(updatedPhone);
                            tvProfileEmail.setText(updatedEmail);

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Profile updated successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    ProfileActivity.this,
                                    "Update failed",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<UserResponse> call,
                            Throwable t
                    ) {

                        Toast.makeText(
                                ProfileActivity.this,
                                "Connection Failed: " + t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void applyTheme() {

        int bg;
        int card;
        int textPrimary;
        int textSecondary;
        int stroke;

        if (isDarkMode) {

            bg = Color.parseColor("#070B1A");
            card = Color.parseColor("#1B2236");
            textPrimary = Color.WHITE;
            textSecondary = Color.parseColor("#B8C1EC");
            stroke = Color.parseColor("#2F3A5A");

            btnThemeProfile.setText("☀");

        } else {

            bg = Color.WHITE;
            card = Color.parseColor("#F3F4F6");
            textPrimary = Color.BLACK;
            textSecondary = Color.parseColor("#6B7280");
            stroke = Color.parseColor("#D1D5DB");

            btnThemeProfile.setText("☾");
        }

        getWindow().getDecorView().setBackgroundColor(bg);

        mainLayout.setBackgroundColor(bg);

        View root = findViewById(android.R.id.content);
        root.setBackgroundColor(bg);

        tvTitle.setTextColor(textPrimary);
        btnBack.setTextColor(textPrimary);

        tvProfileEmail.setTextColor(textSecondary);

        btnThemeProfile.setTextColor(textPrimary);
        btnThemeProfile.setBackground(
                circle(card, stroke)
        );

        styleEditText(etName, card, textPrimary, textSecondary, stroke);
        styleEditText(etEmail, card, textPrimary, textSecondary, stroke);
        styleEditText(etPhone, card, textPrimary, textSecondary, stroke);

        btnSave.setTextColor(Color.WHITE);
    }

    private GradientDrawable circle(int color, int strokeColor) {

        GradientDrawable d = new GradientDrawable();

        d.setShape(GradientDrawable.OVAL);

        d.setColor(color);

        d.setStroke(2, strokeColor);

        return d;
    }
    private void styleEditText(
            EditText editText,
            int bg,
            int text,
            int hint,
            int stroke
    ) {

        editText.setTextColor(text);

        editText.setHintTextColor(hint);

        GradientDrawable drawable = new GradientDrawable();

        drawable.setCornerRadius(28);

        drawable.setColor(bg);

        drawable.setStroke(2, stroke);

        editText.setBackground(drawable);
    }
}