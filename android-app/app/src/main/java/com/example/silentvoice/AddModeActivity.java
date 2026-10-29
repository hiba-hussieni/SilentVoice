package com.example.silentvoice;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import com.example.silentvoice.api.AddModeWithGestureRequest;
import com.example.silentvoice.api.ApiService;
import com.example.silentvoice.api.TranslationResponse;
import retrofit2.Call;
import retrofit2.Response;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Intent;
public class AddModeActivity extends AppCompatActivity {

    ScrollView rootScroll;

    LinearLayout startCard, statusCard, fingerStatesCard, manualPanel, simulationPanel;
    LinearLayout gesturePreviewCard;
    LinearLayout cardThumb, cardIndex, cardMiddle, cardRing, cardPinky;

    EditText etModeName, etModeWord;
    Button btnAddMode;

    TextView profileCircle;
    TextView btnThemeToggle, btnManualMode, btnSimulationMode;
    TextView tvGreeting, tvUserName;
    TextView tvStartTitle, tvStartSubtitle;
    TextView tvStatusLabel, tvCurrentMode, tvConnectionStatus;
    TextView tvFingerTitle, tvPattern;
    TextView tvThumbLabel, tvIndexLabel, tvMiddleLabel, tvRingLabel, tvPinkyLabel;
    TextView tvThumbState, tvIndexState, tvMiddleState, tvRingState, tvPinkyState;
    TextView tvManualTitle, tvManualSubtitle;
    TextView tvThumbValue, tvIndexValue, tvMiddleValue, tvRingValue, tvPinkyValue;


    SeekBar seekThumb, seekIndex, seekMiddle, seekRing, seekPinky;
    SeekBar seekAccX, seekAccY, seekAccZ;
    SeekBar seekGyroX, seekGyroY, seekGyroZ;
    boolean isDarkMode;
    boolean isManualMode = true;
    TextView btnBackAdd;
    TextView tvLiveJsonAdd, tvAccValuesAdd, tvGyroValuesAdd;
    HandAnimationView handAnimationView;

    String selectedColor = "#7B61FF";
    String selectedIcon = "home";
    private final String WOKWI_URL = "http://10.0.2.2:8180/data";

    private final Handler wokwiHandler =
            new Handler(Looper.getMainLooper());

    private boolean wokwiRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mode);
        isDarkMode = ThemeManager.isDarkMode(this);

        initViews();
        setupUserProfile();

        setupClicks();
        applyTheme();
        setupSliders();
        updateAddModeFingerStates();
    }

    private void initViews() {
        rootScroll = findViewById(R.id.rootScroll);

        startCard = findViewById(R.id.startCard);
        statusCard = findViewById(R.id.statusCard);
        fingerStatesCard = findViewById(R.id.fingerStatesCard);
        manualPanel = findViewById(R.id.manualPanel);
        simulationPanel = findViewById(R.id.simulationPanel);
        gesturePreviewCard = findViewById(R.id.gesturePreviewCard);
        handAnimationView = findViewById(R.id.handAnimationView);

        cardThumb = findViewById(R.id.cardThumb);
        cardIndex = findViewById(R.id.cardIndex);
        cardMiddle = findViewById(R.id.cardMiddle);
        cardRing = findViewById(R.id.cardRing);
        cardPinky = findViewById(R.id.cardPinky);

        etModeName = findViewById(R.id.etModeName);
        etModeWord = findViewById(R.id.etModeWord);
        btnAddMode = findViewById(R.id.btnAddMode);

        profileCircle = findViewById(R.id.profileCircle);

        btnThemeToggle = findViewById(R.id.btnThemeToggle);
        btnManualMode = findViewById(R.id.btnManualMode);
        btnSimulationMode = findViewById(R.id.btnSimulationMode);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvUserName = findViewById(R.id.tvUserName);

        tvStartTitle = findViewById(R.id.tvStartTitle);
        tvStartSubtitle = findViewById(R.id.tvStartSubtitle);

        tvStatusLabel = findViewById(R.id.tvStatusLabel);
        tvCurrentMode = findViewById(R.id.tvCurrentMode);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);

        tvFingerTitle = findViewById(R.id.tvFingerTitle);
        tvPattern = findViewById(R.id.tvPattern);

        tvThumbLabel = findViewById(R.id.tvThumbLabel);
        tvIndexLabel = findViewById(R.id.tvIndexLabel);
        tvMiddleLabel = findViewById(R.id.tvMiddleLabel);
        tvRingLabel = findViewById(R.id.tvRingLabel);
        tvPinkyLabel = findViewById(R.id.tvPinkyLabel);

        tvThumbState = findViewById(R.id.tvThumbState);
        tvIndexState = findViewById(R.id.tvIndexState);
        tvMiddleState = findViewById(R.id.tvMiddleState);
        tvRingState = findViewById(R.id.tvRingState);
        tvPinkyState = findViewById(R.id.tvPinkyState);

        tvManualTitle = findViewById(R.id.tvManualTitle);
        tvManualSubtitle = findViewById(R.id.tvManualSubtitle);

        tvThumbValue = findViewById(R.id.tvThumbValue);
        tvIndexValue = findViewById(R.id.tvIndexValue);
        tvMiddleValue = findViewById(R.id.tvMiddleValue);
        tvRingValue = findViewById(R.id.tvRingValue);
        tvPinkyValue = findViewById(R.id.tvPinkyValue);

        seekThumb = findViewById(R.id.seekThumb);
        seekIndex = findViewById(R.id.seekIndex);
        seekMiddle = findViewById(R.id.seekMiddle);
        seekRing = findViewById(R.id.seekRing);
        seekPinky = findViewById(R.id.seekPinky);
        btnBackAdd = findViewById(R.id.btnBackAdd);
        seekAccX = findViewById(R.id.seekAccX);
        seekAccY = findViewById(R.id.seekAccY);
        seekAccZ = findViewById(R.id.seekAccZ);

        seekGyroX = findViewById(R.id.seekGyroX);
        seekGyroY = findViewById(R.id.seekGyroY);
        seekGyroZ = findViewById(R.id.seekGyroZ);
        btnBackAdd.setOnClickListener(v -> finish());
        tvLiveJsonAdd = findViewById(R.id.tvLiveJsonAdd);
        tvAccValuesAdd = findViewById(R.id.tvAccValuesAdd);
        tvGyroValuesAdd = findViewById(R.id.tvGyroValuesAdd);
      //  tvHandOrientationAdd = findViewById(R.id.tvHandOrientationAdd);

    }

    private void setupClicks() {

        btnThemeToggle.setOnClickListener(v -> {

            isDarkMode = !isDarkMode;

            ThemeManager.saveTheme(
                    AddModeActivity.this,
                    isDarkMode
            );

            applyTheme();
        });

        btnManualMode.setOnClickListener(v -> {

            isManualMode = true;

            manualPanel.setVisibility(View.VISIBLE);
            simulationPanel.setVisibility(View.GONE);

            stopWokwiStream();

            applyTheme();
        });

        btnSimulationMode.setOnClickListener(v -> {
            isManualMode = false;

            manualPanel.setVisibility(View.GONE);
            simulationPanel.setVisibility(View.VISIBLE);

            startWokwiStream();

            applyTheme();
        });


        btnAddMode.setOnClickListener(v -> addMode());

        profileCircle.setOnClickListener(v -> {
            Intent intent = new Intent(AddModeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
    private void setupUserProfile() {

        SharedPreferences profilePrefs =
                getSharedPreferences("USER_PROFILE", MODE_PRIVATE);

        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        String savedName = profilePrefs.getString(
                "name",
                userPrefs.getString("username", "Guest")
        );

        String savedEmail = profilePrefs.getString(
                "email",
                userPrefs.getString("email", "")
        );

        tvGreeting.setText("Good Afternoon");

        if (savedEmail != null && !savedEmail.trim().isEmpty()) {

            tvUserName.setText(savedEmail);

            if (savedName != null &&
                    !savedName.trim().isEmpty() &&
                    !savedName.equals("Guest")) {

                profileCircle.setText(
                        savedName.substring(0, 1).toUpperCase()
                );

            } else {

                profileCircle.setText(
                        savedEmail.substring(0, 1).toUpperCase()
                );
            }

        } else if (savedName != null &&
                !savedName.trim().isEmpty() &&
                !savedName.equals("Guest")) {

            tvUserName.setText(savedName);

            profileCircle.setText(
                    savedName.substring(0, 1).toUpperCase()
            );

        } else {

            tvUserName.setText("Guest");

            profileCircle.setText("👤");
        }
    }
    private void setupSliders() {

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateAddModeFingerStates();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        seekThumb.setOnSeekBarChangeListener(listener);
        seekIndex.setOnSeekBarChangeListener(listener);
        seekMiddle.setOnSeekBarChangeListener(listener);
        seekRing.setOnSeekBarChangeListener(listener);
        seekPinky.setOnSeekBarChangeListener(listener);
        SeekBar.OnSeekBarChangeListener imuListener =
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser
                    ) {

                        TextView tvManualAccX =
                                findViewById(R.id.tvAccXValue);

                        TextView tvManualAccY =
                                findViewById(R.id.tvAccYValue);

                        TextView tvManualAccZ =
                                findViewById(R.id.tvAccZValue);

                        TextView tvManualGyroX =
                                findViewById(R.id.tvGyroXValue);

                        TextView tvManualGyroY =
                                findViewById(R.id.tvGyroYValue);

                        TextView tvManualGyroZ =
                                findViewById(R.id.tvGyroZValue);

                        tvManualAccX.setText(
                                "Acc X: " +
                                        (seekAccX.getProgress() - 50)
                        );

                        tvManualAccY.setText(
                                "Acc Y: " +
                                        (seekAccY.getProgress() - 50)
                        );

                        tvManualAccZ.setText(
                                "Acc Z: " +
                                        (seekAccZ.getProgress() - 50)
                        );

                        tvManualGyroX.setText(
                                "Gyro X: " +
                                        (seekGyroX.getProgress() - 50)
                        );

                        tvManualGyroY.setText(
                                "Gyro Y: " +
                                        (seekGyroY.getProgress() - 50)
                        );

                        tvManualGyroZ.setText(
                                "Gyro Z: " +
                                        (seekGyroZ.getProgress() - 50)
                        );
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar
                    ) {}

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar
                    ) {}
                };

        seekAccX.setOnSeekBarChangeListener(imuListener);
        seekAccY.setOnSeekBarChangeListener(imuListener);
        seekAccZ.setOnSeekBarChangeListener(imuListener);

        seekGyroX.setOnSeekBarChangeListener(imuListener);
        seekGyroY.setOnSeekBarChangeListener(imuListener);
        seekGyroZ.setOnSeekBarChangeListener(imuListener);
    }

    private void updateAddModeFingerStates() {

        int thumb = seekThumb.getProgress() * 40;
        int index = seekIndex.getProgress() * 40;
        int middle = seekMiddle.getProgress() * 40;
        int ring = seekRing.getProgress() * 40;
        int pinky = seekPinky.getProgress() * 40;

        String t = state(thumb);
        String i = state(index);
        String m = state(middle);
        String r = state(ring);
        String p = state(pinky);

        tvThumbState.setText(t);
        tvIndexState.setText(i);
        tvMiddleState.setText(m);
        tvRingState.setText(r);
        tvPinkyState.setText(p);

        tvPattern.setText("Pattern: " + t + "," + i + "," + m + "," + r + "," + p);

        tvThumbValue.setText("Thumb: " + seekThumb.getProgress() + "%");
        tvIndexValue.setText("Index: " + seekIndex.getProgress() + "%");
        tvMiddleValue.setText("Middle: " + seekMiddle.getProgress() + "%");
        tvRingValue.setText("Ring: " + seekRing.getProgress() + "%");
        tvPinkyValue.setText("Pinky: " + seekPinky.getProgress() + "%");

        if (handAnimationView != null) {
            handAnimationView.setFingerSensorValues(thumb, index, middle, ring, pinky);
        }
    }

    private String state(int value) {
        if (value < 1300) {
            return "OPEN";
        } else if (value < 2700) {
            return "HALF";
        } else {
            return "CLOSED";
        }
    }
    private void addMode() {
        stopWokwiStream();
        btnAddMode.setEnabled(false);
        btnAddMode.setText("Adding...");
        String modeName = etModeName.getText().toString().trim();
        String word = etModeWord.getText().toString().trim();

        if (modeName.isEmpty()) {
            Toast.makeText(this, "Write mode name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (word.isEmpty()) {
            Toast.makeText(this, "Write word", Toast.LENGTH_SHORT).show();
            return;
        }

        String thumb = state(seekThumb.getProgress() * 40);
        String index = state(seekIndex.getProgress() * 40);
        String middle = state(seekMiddle.getProgress() * 40);
        String ring = state(seekRing.getProgress() * 40);
        String pinky = state(seekPinky.getProgress() * 40);
        int accX = seekAccX.getProgress() - 50;
        int accY = seekAccY.getProgress() - 50;
        int accZ = seekAccZ.getProgress() - 50;

        int gyroX = seekGyroX.getProgress() - 50;
        int gyroY = seekGyroY.getProgress() - 50;
        int gyroZ = seekGyroZ.getProgress() - 50;
        SharedPreferences userPrefs =
                getSharedPreferences("USER_DATA", MODE_PRIVATE);

        int userId = userPrefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this,
                    "User not logged in",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AddModeWithGestureRequest request =
                new AddModeWithGestureRequest(
                        userId,
                        modeName,
                        word,
                        thumb,
                        index,
                        middle,
                        ring,
                        pinky,
                        accX,
                        accY,
                        accZ,
                        gyroX,
                        gyroY,
                        gyroZ
                );

        ApiService apiService = RetrofitClientSpring
                .getClient()
                .create(ApiService.class);

        apiService.addModeWithGesture(request)
                .enqueue(new retrofit2.Callback<TranslationResponse>() {

                    @Override
                    public void onResponse(

                            retrofit2.Call<TranslationResponse> call,
                            retrofit2.Response<TranslationResponse> response

                    ) {
                        btnAddMode.setEnabled(true);
                        btnAddMode.setText("Add Mode");
                        if (response.isSuccessful()) {

                            Toast.makeText(AddModeActivity.this,
                                    "Mode Added Successfully",
                                    Toast.LENGTH_LONG).show();

                            etModeName.setText("");
                            etModeWord.setText("");

                        } else {

                            try {

                                String error = response.errorBody() != null
                                        ? response.errorBody().string()
                                        : "No error body";

                                Toast.makeText(AddModeActivity.this,
                                        "Add failed: " +
                                                response.code() +
                                                "\n" +
                                                error,
                                        Toast.LENGTH_LONG).show();

                            } catch (Exception e) {

                                Toast.makeText(AddModeActivity.this,
                                        "Add failed: " + response.code(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<TranslationResponse> call,
                            Throwable t

                    ) {
                        btnAddMode.setEnabled(true);
                        btnAddMode.setText("Add Mode");
                        Toast.makeText(AddModeActivity.this,
                                "Connection failed: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateSimulationUI(
            int thumb, int index, int middle, int ring, int pinky,
            int accX, int accY, int accZ,
            int gyroX, int gyroY, int gyroZ
    ) {

        tvLiveJsonAdd.setText(
                "thumb: " + thumb + "\n" +
                        "index: " + index + "\n" +
                        "middle: " + middle + "\n" +
                        "ring: " + ring + "\n" +
                        "pinky: " + pinky
        );

        tvAccValuesAdd.setText(
                "ACC: X=" + accX +
                        " Y=" + accY +
                        " Z=" + accZ
        );

        tvGyroValuesAdd.setText(
                "GYRO: X=" + gyroX +
                        " Y=" + gyroY +
                        " Z=" + gyroZ
        );


    }

    private void startWokwiStream() {

        wokwiRunning = true;

        wokwiHandler.post(new Runnable() {
            @Override
            public void run() {

                if (!wokwiRunning) return;

                new Thread(() -> {
                    try {
                        URL url = new URL(WOKWI_URL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);

                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(connection.getInputStream()));

                        StringBuilder result = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        reader.close();
                        connection.disconnect();

                        String data = result.toString().trim();

                        if (data.isEmpty()) return;

                        int thumb, index, middle, ring, pinky;
                        int accX, accY, accZ, gyroX, gyroY, gyroZ;

                        if (data.startsWith("{")) {
                            JSONObject json = new JSONObject(data);

                            thumb = json.getInt("thumb");
                            index = json.getInt("index");
                            middle = json.getInt("middle");
                            ring = json.getInt("ring");
                            pinky = json.getInt("pinky");

                            accX = json.getInt("accX");
                            accY = json.getInt("accY");
                            accZ = json.getInt("accZ");

                            gyroX = json.getInt("gyroX");
                            gyroY = json.getInt("gyroY");
                            gyroZ = json.getInt("gyroZ");

                        } else {
                            String[] v = data.split(",");

                            if (v.length < 11) return;

                            thumb = Integer.parseInt(v[0].trim());
                            index = Integer.parseInt(v[1].trim());
                            middle = Integer.parseInt(v[2].trim());
                            ring = Integer.parseInt(v[3].trim());
                            pinky = Integer.parseInt(v[4].trim());

                            accX = Integer.parseInt(v[5].trim());
                            accY = Integer.parseInt(v[6].trim());
                            accZ = Integer.parseInt(v[7].trim());

                            gyroX = Integer.parseInt(v[8].trim());
                            gyroY = Integer.parseInt(v[9].trim());
                            gyroZ = Integer.parseInt(v[10].trim());
                        }

                        int finalThumb = thumb;
                        int finalIndex = index;
                        int finalMiddle = middle;
                        int finalRing = ring;
                        int finalPinky = pinky;

                        int finalAccX = accX;
                        int finalAccY = accY;
                        int finalAccZ = accZ;

                        int finalGyroX = gyroX;
                        int finalGyroY = gyroY;
                        int finalGyroZ = gyroZ;

                        runOnUiThread(() -> {

                            tvThumbState.setText(state(finalThumb));
                            tvIndexState.setText(state(finalIndex));
                            tvMiddleState.setText(state(finalMiddle));
                            tvRingState.setText(state(finalRing));
                            tvPinkyState.setText(state(finalPinky));

                            tvPattern.setText(
                                    "Pattern: " +
                                            state(finalThumb) + "," +
                                            state(finalIndex) + "," +
                                            state(finalMiddle) + "," +
                                            state(finalRing) + "," +
                                            state(finalPinky)
                            );

                            updateSimulationUI(
                                    finalThumb,
                                    finalIndex,
                                    finalMiddle,
                                    finalRing,
                                    finalPinky,
                                    finalAccX,
                                    finalAccY,
                                    finalAccZ,
                                    finalGyroX,
                                    finalGyroY,
                                    finalGyroZ
                            );


                            if (handAnimationView != null) {
                                handAnimationView.setFingerSensorValues(
                                        finalThumb,
                                        finalIndex,
                                        finalMiddle,
                                        finalRing,
                                        finalPinky
                                );
                            }
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            if (tvLiveJsonAdd != null) {
                                tvLiveJsonAdd.setText("Waiting for Wokwi data...");
                            }
                        });
                    }
                }).start();

                wokwiHandler.postDelayed(this, 1000);
            }
        });
    }

    private void stopWokwiStream() {
        wokwiRunning = false;
        wokwiHandler.removeCallbacksAndMessages(null);
    }
    private void applyTheme() {

        int bg = isDarkMode ? Color.parseColor("#0D0F1A") : Color.parseColor("#F5F6FA");
        int card = isDarkMode ? Color.parseColor("#1A1D2E") : Color.WHITE;
        int mini = isDarkMode ? Color.parseColor("#24283A") : Color.parseColor("#EEF1F8");
        int stroke = isDarkMode ? Color.parseColor("#2B2F45") : Color.parseColor("#DDE1EE");
        int miniStroke = isDarkMode ? Color.parseColor("#383D5C") : Color.parseColor("#D8DDED");
        int primary = isDarkMode ? Color.WHITE : Color.parseColor("#111111");
        int secondary = isDarkMode ? Color.parseColor("#A0A3BD") : Color.parseColor("#666666");

        rootScroll.setBackgroundColor(bg);

        startCard.setBackground(round(card, stroke, 22));
        statusCard.setBackground(round(card, stroke, 22));
        fingerStatesCard.setBackground(round(card, stroke, 22));
        manualPanel.setBackground(round(card, stroke, 22));
        simulationPanel.setBackground(round(card, stroke, 22));


        if (gesturePreviewCard != null) {
            gesturePreviewCard.setBackground(round(card, stroke, 22));
        }

        if (handAnimationView != null) {
            handAnimationView.setDarkMode(isDarkMode);
        }

        cardThumb.setBackground(round(mini, miniStroke, 16));
        cardIndex.setBackground(round(mini, miniStroke, 16));
        cardMiddle.setBackground(round(mini, miniStroke, 16));
        cardRing.setBackground(round(mini, miniStroke, 16));
        cardPinky.setBackground(round(mini, miniStroke, 16));

        profileCircle.setTextColor(Color.WHITE);
        profileCircle.setBackgroundResource(R.drawable.bg_circle);

        tvGreeting.setTextColor(secondary);
        tvUserName.setTextColor(primary);

        tvStartTitle.setTextColor(primary);
        tvStartSubtitle.setTextColor(secondary);

        tvStatusLabel.setTextColor(secondary);
        tvCurrentMode.setTextColor(Color.parseColor("#7B61FF"));
        tvConnectionStatus.setTextColor(secondary);

        tvFingerTitle.setTextColor(primary);
        tvPattern.setTextColor(secondary);

        tvThumbLabel.setTextColor(secondary);
        tvIndexLabel.setTextColor(secondary);
        tvMiddleLabel.setTextColor(secondary);
        tvRingLabel.setTextColor(secondary);
        tvPinkyLabel.setTextColor(secondary);

        tvManualTitle.setTextColor(primary);
        tvManualSubtitle.setTextColor(secondary);

        tvThumbValue.setTextColor(primary);
        tvIndexValue.setTextColor(primary);
        tvMiddleValue.setTextColor(primary);
        tvRingValue.setTextColor(primary);
        tvPinkyValue.setTextColor(primary);

        // =========================
        // FIX MANUAL IMU TEXT
        // =========================

        TextView tvManualAccX = findViewById(R.id.tvAccXValue);
        TextView tvManualAccY = findViewById(R.id.tvAccYValue);
        TextView tvManualAccZ = findViewById(R.id.tvAccZValue);

        TextView tvManualGyroX = findViewById(R.id.tvGyroXValue);
        TextView tvManualGyroY = findViewById(R.id.tvGyroYValue);
        TextView tvManualGyroZ = findViewById(R.id.tvGyroZValue);

        if (tvManualAccX != null)
            tvManualAccX.setTextColor(primary);

        if (tvManualAccY != null)
            tvManualAccY.setTextColor(primary);

        if (tvManualAccZ != null)
            tvManualAccZ.setTextColor(primary);

        if (tvManualGyroX != null)
            tvManualGyroX.setTextColor(primary);

        if (tvManualGyroY != null)
            tvManualGyroY.setTextColor(primary);

        if (tvManualGyroZ != null)
            tvManualGyroZ.setTextColor(primary);

        // =========================

        etModeName.setTextColor(primary);
        etModeName.setHintTextColor(secondary);
        etModeName.setBackground(round(mini, miniStroke, 16));

        etModeWord.setTextColor(primary);
        etModeWord.setHintTextColor(secondary);
        etModeWord.setBackground(round(mini, miniStroke, 16));

        btnThemeToggle.setText(isDarkMode ? "☀" : "☾");
        btnThemeToggle.setTextColor(primary);
        btnThemeToggle.setBackground(round(mini, miniStroke, 18));

        btnAddMode.setTextColor(Color.WHITE);
        btnAddMode.setBackground(
                round(
                        Color.parseColor("#7B61FF"),
                        Color.parseColor("#7B61FF"),
                        18
                )
        );

        if (tvLiveJsonAdd != null) {

            tvLiveJsonAdd.setTextColor(primary);
            tvLiveJsonAdd.setBackground(round(mini, miniStroke, 16));

            tvAccValuesAdd.setTextColor(primary);
            tvAccValuesAdd.setBackground(round(mini, miniStroke, 16));

            tvGyroValuesAdd.setTextColor(primary);
            tvGyroValuesAdd.setBackground(round(mini, miniStroke, 16));

          //  tvHandOrientationAdd.setTextColor(primary);
         //   tvHandOrientationAdd.setBackground(round(mini, miniStroke, 16));
        }

        if (isDarkMode) {

            btnBackAdd.setTextColor(Color.WHITE);

            btnBackAdd.setBackground(
                    round(
                            Color.BLACK,
                            Color.BLACK,
                            100
                    )
            );

        } else {

            btnBackAdd.setTextColor(Color.BLACK);

            btnBackAdd.setBackground(
                    round(
                            Color.WHITE,
                            Color.parseColor("#E5E7EB"),
                            100
                    )
            );
        }

        refreshModeButtons(mini, miniStroke, primary);
    }
    @Override
    protected void onResume() {
        super.onResume();

        setupUserProfile();

        isDarkMode = ThemeManager.isDarkMode(this);

        applyTheme();
    }
    private void refreshModeButtons(int inactive, int inactiveStroke, int inactiveText) {
        if (isManualMode) {
            btnManualMode.setBackground(round(Color.parseColor("#7B61FF"), Color.parseColor("#7B61FF"), 18));
            btnManualMode.setTextColor(Color.WHITE);

            btnSimulationMode.setBackground(round(inactive, inactiveStroke, 18));
            btnSimulationMode.setTextColor(inactiveText);
        } else {
            btnSimulationMode.setBackground(round(Color.parseColor("#7B61FF"), Color.parseColor("#7B61FF"), 18));
            btnSimulationMode.setTextColor(Color.WHITE);

            btnManualMode.setBackground(round(inactive, inactiveStroke, 18));
            btnManualMode.setTextColor(inactiveText);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWokwiStream();
    }
    private GradientDrawable round(int color, int strokeColor, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        d.setStroke(1, strokeColor);
        return d;
    }
}