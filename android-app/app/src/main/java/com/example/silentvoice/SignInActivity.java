package com.example.silentvoice;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silentvoice.api.ApiService;
import com.example.silentvoice.api.RegisterRequest;
import com.example.silentvoice.api.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    FrameLayout root;
    ImageView imgBgSignIn;
    LinearLayout signInCard;

    TextView btnThemeSignIn, tvTitleSignIn, tvSubtitleSignIn;
    EditText etName, etEmail, etPassword;
    Button btnCreateAccount;

    boolean isDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        isDark = ThemeManager.isDarkMode(this);

        root = findViewById(R.id.root);
        imgBgSignIn = findViewById(R.id.imgBgSignIn);
        signInCard = findViewById(R.id.signInCard);

        btnThemeSignIn = findViewById(R.id.btnThemeSignIn);
        tvTitleSignIn = findViewById(R.id.tvTitleSignIn);
        tvSubtitleSignIn = findViewById(R.id.tvSubtitleSignIn);

        etName = findViewById(R.id.etFullNameSignIn);
        etEmail = findViewById(R.id.etEmailSignIn);
        etPassword = findViewById(R.id.etPasswordSignIn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        applyTheme();

        btnThemeSignIn.setOnClickListener(v -> {

            isDark = !isDark;

            ThemeManager.saveTheme(
                    SignInActivity.this,
                    isDark
            );

            applyTheme();
        });
        btnCreateAccount.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields ⚠️", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Enter valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCreateAccount.setEnabled(false);
        btnCreateAccount.setText("Please wait...");

        ApiService api = RetrofitClient.getClient().create(ApiService.class);
        RegisterRequest request = new RegisterRequest(name, email, password);

        api.register(request).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                btnCreateAccount.setEnabled(true);
                btnCreateAccount.setText("Create Account");

                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(
                            SignInActivity.this,
                            "Account Created 🎉 Please login",
                            Toast.LENGTH_LONG
                    ).show();

                    etName.setText("");
                    etEmail.setText("");
                    etPassword.setText("");

                    Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(
                            SignInActivity.this,
                            "Email already exists ❌",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

                btnCreateAccount.setEnabled(true);
                btnCreateAccount.setText("Create Account");

                Toast.makeText(
                        SignInActivity.this,
                        "Connection Failed: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void applyTheme() {

        if (isDark) {

            imgBgSignIn.setImageResource(R.drawable.signin_dark);

            btnThemeSignIn.setText("☀");
            btnThemeSignIn.setTextColor(Color.WHITE);
            btnThemeSignIn.setBackground(round("#121829", "#2B3250", 16));

            signInCard.setBackground(round("#CC151A2E", "#4B537A", 24));

            tvTitleSignIn.setTextColor(Color.WHITE);
            tvSubtitleSignIn.setTextColor(Color.parseColor("#D6D8E8"));

            styleInput(etName, "#22283D", "#3A4160", "#FFFFFF", "#A8ADC6");
            styleInput(etEmail, "#22283D", "#3A4160", "#FFFFFF", "#A8ADC6");
            styleInput(etPassword, "#22283D", "#3A4160", "#FFFFFF", "#A8ADC6");

        } else {

            imgBgSignIn.setImageResource(R.drawable.signin_light);

            btnThemeSignIn.setText("☾");
            btnThemeSignIn.setTextColor(Color.parseColor("#111827"));
            btnThemeSignIn.setBackground(round("#FFFFFF", "#E5E7EB", 16));

            signInCard.setBackground(round("#CCFFFFFF", "#E7D8FF", 24));

            tvTitleSignIn.setTextColor(Color.parseColor("#2D1B63"));
            tvSubtitleSignIn.setTextColor(Color.parseColor("#6B6280"));

            styleInput(etName, "#F8F5FF", "#E4DAF7", "#1F2937", "#9CA3AF");
            styleInput(etEmail, "#F8F5FF", "#E4DAF7", "#1F2937", "#9CA3AF");
            styleInput(etPassword, "#F8F5FF", "#E4DAF7", "#1F2937", "#9CA3AF");
        }

        btnCreateAccount.setTextColor(Color.WHITE);
    }

    private void styleInput(EditText editText,
                            String bgColor,
                            String strokeColor,
                            String textColor,
                            String hintColor) {

        editText.setBackground(round(bgColor, strokeColor, 14));
        editText.setTextColor(Color.parseColor(textColor));
        editText.setHintTextColor(Color.parseColor(hintColor));
    }

    private GradientDrawable round(String color, String strokeColor, int radiusDp) {

        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor(color));
        d.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        d.setStroke(1, Color.parseColor(strokeColor));

        return d;
    }
}