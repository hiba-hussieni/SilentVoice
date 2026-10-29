package com.example.silentvoice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.silentvoice.api.*;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
public class MainActivity extends AppCompatActivity {

    private final String WOKWI_URL = "http://10.0.2.2:8180/data";
    private TextToSpeech tts;
    boolean isTtsReady = false;
    ScrollView rootScroll;
    boolean isAiSuggestionInProgress = false;
    LineChart imuLineChart;
    ImageView btnSpeak;
    Button btnRefreshSuggestions;
    LinearLayout statusCard, resultCard, fingerStatesCard, manualPanel, simulationPanel, handAnimationCard, suggestionsContainer;
    LinearLayout cardThumb, cardIndex, cardMiddle, cardRing, cardPinky;
    LinearLayout customCard, historyCard, historyList;
    ImageView imgPeople;
    HandAnimationView handAnimationView;
    TextView btnManualMode, btnSimulationMode, btnThemeToggle;
    TextView modeHome, modeEmergency, modeHealth;

    TextView tvAppName, tvAppSubtitle, tvStatusLabel, tvCurrentMode, tvConnectionStatus;
    TextView tvResultLabel, tvDetectedWord, tvDetectedMeaning, tvFutureSentence;
    TextView tvFingerTitle, tvPattern, tvHandAnimationTitle, tvHandAnimationSubtitle;
    TextView tvThumbLabel, tvIndexLabel, tvMiddleLabel, tvRingLabel, tvPinkyLabel;
    TextView tvThumbState, tvIndexState, tvMiddleState, tvRingState, tvPinkyState;
    TextView tvManualTitle, tvManualSubtitle, tvSimulationTitle, tvSimulationSubtitle, tvLiveJson, tvChartTitle;
    TextView tvThumbValue, tvIndexValue, tvMiddleValue, tvRingValue, tvPinkyValue;
    TextView tvCustomTitle, tvCurrentPattern, tvHistoryTitle;
    LinearLayout startCard;
    TextView tvStartTitle, tvStartSubtitle, btnStartNow;
    EditText etCustomWord;

    SeekBar seekThumb, seekIndex, seekMiddle, seekRing, seekPinky;
    Button  btnSOS, btnSaveGesture;
    long currentModeId = 1;
    LineChart liveLineChart ;
    LineDataSet thumbSet, indexSet, middleSet, ringSet, pinkySet;
    LineData lineData;
    private TextView tvAccValues, tvGyroValues;
    Handler handler = new Handler(Looper.getMainLooper());
    private TextView tvHandOrientation;
    HashMap<String, String> customGestures = new HashMap<>();
    String lastMeaning = "";
    boolean isDarkMode;
    boolean isManualMode = true;
    boolean isSimulationRunning = false;
    private SeekBar seekAccX, seekAccY, seekAccZ;
    private TextView tvManualAccX, tvManualAccY, tvManualAccZ;
    int timeIndex = 0;
    String currentPatternKey = "";
    TextView tvGreeting, tvUserName;
    TextView profileIcon;
    String lastHistoryEntry = "";
    String activeAppMode = "Default";
    String customModeName = "";
    long customModeId = -1;
    LinearLayout loginCard;
    TextView tvLoginTitle, tvLoginStatus;
    EditText etLoginEmail, etLoginPassword;
    private SeekBar seekGyroX, seekGyroY, seekGyroZ;
    private TextView tvManualGyroX, tvManualGyroY, tvManualGyroZ;
    Button btnLogin;
    boolean sosPageOpened = false;
    boolean isFirstLoad = true;
    int loggedInUserId = -1;
    String loggedInUserName = "";
    String lastSentPattern = "";
    long lastSendTime = 0;
    boolean isTranslateInProgress = false;
    int wokwiFailCount = 0;
    private int suggestionRequestId = 0;
    TextView tvSuggestion1, tvSuggestion2, tvSuggestion3;
    String lastSuggestionKey = "";
    String lastAiGesture = "";
    long lastAiTime = 0;
    String pendingPatternKey = "";
    long patternStableStart = 0;
    final long PATTERN_STABLE_TIME = 250;
    boolean isWokwiFetchInProgress = false;
    HashMap<String, java.util.List<String>> suggestionsCache =
            new HashMap<>();
    Spinner spinnerLanguage;
    Button btnTranslateSuggestion;
    TextView tvTranslatedSuggestion;

    String selectedSuggestion = "";
    Runnable liveRunnable = new Runnable() {
        @Override
        public void run() {
            if (isSimulationRunning) {
                fetchWokwiData();
                handler.postDelayed(this, 1000);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isDarkMode = ThemeManager.isDarkMode(this);

        initViews();

        Intent sosIntent = new Intent(this, SOSMonitorService.class);
        sosIntent.putExtra("enable_sos_monitor", true);
        startService(sosIntent);
        // 🔥 زر Start Now → Modes
        btnStartNow.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ModesActivity.class);
            intent.putExtra("username", tvUserName.getText().toString());
            startActivity(intent);
        });

        // 🔥 استلام المود من Discover
        String selectedMode = getIntent().getStringExtra("mode");
        long selectedModeId = getIntent().getLongExtra("modeId", -1);

        if (selectedMode != null && selectedModeId != -1) {
            customModeName = selectedMode;
            customModeId = selectedModeId;

            modeHealth.setText(selectedMode);

            currentModeId = selectedModeId;
            setAppMode(selectedMode);
            updateMode(selectedModeId);
            Log.d("MODE_ID", "MODE ID = " + currentModeId);
        }
        // باقي التهيئة
        initTts();
        setupClicks();
        setupLanguageSpinner();
        setupSliders();
        setupChart();
        setupImuChart();

        applyTheme();
        showManualMode();
        new Handler().postDelayed(() -> {
            isFirstLoad = false;
        }, 500);

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        String selectedMode = intent.getStringExtra("mode");
        long selectedModeId = intent.getLongExtra("modeId", -1);
        int userId = intent.getIntExtra("userId", -1);

        if (userId != -1) {
            loggedInUserId = userId;
        }

        if (selectedMode != null && selectedModeId != -1) {
            customModeName = selectedMode;
            customModeId = selectedModeId;
            updateMode(selectedModeId);
            modeHealth.setText(selectedMode);

            currentModeId = selectedModeId;
            setAppMode(selectedMode);

            Log.d("MODE_ID", "MODE ID = " + currentModeId);
        }
    }
    private void initTts() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.ENGLISH);

                isTtsReady = true;
                Log.d("VOICE", "TTS READY result=" + result);

                tts.setSpeechRate(1.0f);
                tts.setPitch(1.0f);
            } else {
                Log.d("VOICE", "TTS INIT FAILED");
            }
        });
    }

    private void speak(String text) {
        if (!isTtsReady) {
            Log.d("VOICE", "TTS not ready yet");
            return;
        }

        if (text == null || text.trim().isEmpty()) return;

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void initViews() {
        rootScroll = findViewById(R.id.rootScroll);

        statusCard = findViewById(R.id.statusCard);
        resultCard = findViewById(R.id.resultCard);
        suggestionsContainer = findViewById(R.id.suggestionsContainer);
        fingerStatesCard = findViewById(R.id.fingerStatesCard);
        handAnimationCard = findViewById(R.id.handAnimationCard);
        handAnimationView = findViewById(R.id.handAnimationView);
        tvHandAnimationTitle = findViewById(R.id.tvHandAnimationTitle);
        tvHandAnimationSubtitle = findViewById(R.id.tvHandAnimationSubtitle);
        manualPanel = findViewById(R.id.manualPanel);
        simulationPanel = findViewById(R.id.simulationPanel);
        customCard = findViewById(R.id.customCard);
        historyCard = findViewById(R.id.historyCard);
        historyList = findViewById(R.id.historyList);

        cardThumb = findViewById(R.id.cardThumb);
        cardIndex = findViewById(R.id.cardIndex);
        cardMiddle = findViewById(R.id.cardMiddle);
        cardRing = findViewById(R.id.cardRing);
        cardPinky = findViewById(R.id.cardPinky);

        btnManualMode = findViewById(R.id.btnManualMode);
        btnSimulationMode = findViewById(R.id.btnSimulationMode);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);

        modeHome = findViewById(R.id.modeHome);
        modeEmergency = findViewById(R.id.modeEmergency);
        modeHealth = findViewById(R.id.modeHealth);

        //tvAppName = findViewById(R.id.tvAppName);
        //  tvAppSubtitle = findViewById(R.id.tvAppSubtitle);
        tvStatusLabel = findViewById(R.id.tvStatusLabel);
        tvCurrentMode = findViewById(R.id.tvCurrentMode);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
        tvDetectedMeaning = findViewById(R.id.tvDetectedMeaning);
        tvResultLabel = findViewById(R.id.tvResultLabel);
        tvDetectedWord = findViewById(R.id.tvDetectedWord);
       // tvFutureSentence = findViewById(R.id.tvFutureSentence);
        tvSuggestion1 = findViewById(R.id.tvSuggestion1);
        tvSuggestion2 = findViewById(R.id.tvSuggestion2);
        tvSuggestion3 = findViewById(R.id.tvSuggestion3);
        tvFingerTitle = findViewById(R.id.tvFingerTitle);
        tvPattern = findViewById(R.id.tvPattern);
        btnRefreshSuggestions = findViewById(R.id.btnRefreshSuggestions);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        btnTranslateSuggestion = findViewById(R.id.btnTranslateSuggestion);
        tvTranslatedSuggestion = findViewById(R.id.tvTranslatedSuggestion);
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
        tvSimulationTitle = findViewById(R.id.tvSimulationTitle);
        tvSimulationSubtitle = findViewById(R.id.tvSimulationSubtitle);
        tvLiveJson = findViewById(R.id.tvLiveJson);
        tvChartTitle = findViewById(R.id.tvChartTitle);

        tvThumbValue = findViewById(R.id.tvThumbValue);
        tvIndexValue = findViewById(R.id.tvIndexValue);
        tvMiddleValue = findViewById(R.id.tvMiddleValue);
        tvRingValue = findViewById(R.id.tvRingValue);
        tvPinkyValue = findViewById(R.id.tvPinkyValue);

        tvCustomTitle = findViewById(R.id.tvCustomTitle);
        tvCurrentPattern = findViewById(R.id.tvCurrentPattern);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);

        etCustomWord = findViewById(R.id.etCustomWord);

        seekThumb = findViewById(R.id.seekThumb);
        seekIndex = findViewById(R.id.seekIndex);
        seekMiddle = findViewById(R.id.seekMiddle);
        seekRing = findViewById(R.id.seekRing);
        seekPinky = findViewById(R.id.seekPinky);

       // btnMockPacket = findViewById(R.id.btnMockPacket);
        btnSOS = findViewById(R.id.btnSOS);
        btnSaveGesture = findViewById(R.id.btnSaveGesture);
        btnStartNow = findViewById(R.id.btnStartNow);
        liveLineChart = findViewById(R.id.liveLineChart);
       /*loginCard = findViewById(R.id.loginCard);
        tvLoginTitle = findViewById(R.id.tvLoginTitle);
        tvLoginStatus = findViewById(R.id.tvLoginStatus);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);*/
        btnLogin = findViewById(R.id.btnLogin);
        tvAccValues = findViewById(R.id.tvAccValues);
        tvGyroValues = findViewById(R.id.tvGyroValues);
        tvAccValues = findViewById(R.id.tvAccValues);
        tvGyroValues = findViewById(R.id.tvGyroValues);
        seekAccX = findViewById(R.id.seekAccX);
        seekAccY = findViewById(R.id.seekAccY);
        seekAccZ = findViewById(R.id.seekAccZ);

        tvManualAccX = findViewById(R.id.tvManualAccX);
        tvManualAccY = findViewById(R.id.tvManualAccY);
        tvManualAccZ = findViewById(R.id.tvManualAccZ);
        tvHandOrientation = findViewById(R.id.tvHandOrientation);
        seekGyroX = findViewById(R.id.seekGyroX);
        seekGyroY = findViewById(R.id.seekGyroY);
        seekGyroZ = findViewById(R.id.seekGyroZ);

        tvManualGyroX = findViewById(R.id.tvManualGyroX);
        tvManualGyroY = findViewById(R.id.tvManualGyroY);
        tvManualGyroZ = findViewById(R.id.tvManualGyroZ);
        imuLineChart = findViewById(R.id.imuLineChart);
        liveLineChart = findViewById(R.id.liveLineChart);
        btnSpeak = findViewById(R.id.btnSpeak);
        tvGreeting = findViewById(R.id.tvGreeting);
        profileIcon = findViewById(R.id.profileIcon);
        tvUserName = findViewById(R.id.tvUserName);
        imgPeople = findViewById(R.id.imgPeople);
        startCard = findViewById(R.id.startCard);
        tvStartTitle = findViewById(R.id.tvStartTitle);
        tvStartSubtitle = findViewById(R.id.tvStartSubtitle);
        btnStartNow = findViewById(R.id.btnStartNow);
        loggedInUserId = getIntent().getIntExtra("userId", -1);
        Log.d("LOGIN_DEBUG", "MAIN USER ID: " + loggedInUserId);
        setupUserProfile();
    }

    private void setupClicks() {
        btnSpeak.setOnClickListener(view -> {
            String text = lastMeaning;

            if (text == null || text.trim().isEmpty()) {
                text = tvDetectedMeaning.getText().toString();
            }

            speak(text);
        });
        btnManualMode.setOnClickListener(v -> showManualMode());
        btnSimulationMode.setOnClickListener(v -> showSimulationMode());

        modeHome.setOnClickListener(v -> {
            currentModeId = 1;
            setAppMode("Default");
            updateMode(1);
        });

        modeEmergency.setOnClickListener(v -> {
            currentModeId = 2;
            setAppMode("Emergency");
            updateMode(2);
        });

        modeHealth.setOnClickListener(v -> {
            if (customModeId != -1) {
                currentModeId = customModeId;
                setAppMode(customModeName);
                updateMode(customModeId);
            }
        });

        btnThemeToggle.setOnClickListener(v -> {

            isDarkMode = !isDarkMode;

            ThemeManager.saveTheme(
                    MainActivity.this,
                    isDarkMode
            );

            applyTheme();

            refreshButtons();

            refreshAppModeButtons();
            setupLanguageSpinner();
        });
        btnSaveGesture.setOnClickListener(view -> {
            String word = etCustomWord.getText().toString().trim();

            if (word.isEmpty()) {
                tvCurrentPattern.setText("Please enter a word first.");
                return;
            }

            if (currentPatternKey.isEmpty()) {
                tvCurrentPattern.setText("Move sliders or run simulation first.");
                return;
            }

            customGestures.put(currentPatternKey, word);
            tvDetectedWord.setText(word);
            tvDetectedMeaning.setText("Custom gesture saved locally");
            tvCurrentPattern.setText("Saved: " + word + " = " + currentPatternKey);
            etCustomWord.setText("");
            addToHistory(word, "Custom gesture saved locally");
        });

      //  btnMockPacket.setOnClickListener(v -> fetchWokwiData());

        btnSOS.setOnClickListener(v -> {
            tvDetectedWord.setText("SOS");
            tvDetectedMeaning.setText("Emergency signal activated");
            setAppMode("Emergency");
            addToHistory("SOS", "Emergency signal activated");
        });
        // btnLogin.setOnClickListener(v -> loginUser());


        profileIcon.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            ProfileActivity.class
                    );

            startActivity(intent);
        });


        btnTranslateSuggestion.setOnClickListener(v -> {

            if (selectedSuggestion == null ||
                    selectedSuggestion.trim().isEmpty()) {

                tvTranslatedSuggestion.setText(
                        "Please select a suggestion first"
                );

                return;
            }

            String selectedLanguage =
                    spinnerLanguage
                            .getSelectedItem()
                            .toString();

            ApiServiceSpring api =
                    RetrofitClientSpring
                            .getClient()
                            .create(ApiServiceSpring.class);

            java.util.Map<String, String> request =
                    new java.util.HashMap<>();

            request.put("text", selectedSuggestion);
            request.put("language", selectedLanguage);

            api.translateSuggestion(request)
                    .enqueue(new Callback<java.util.Map<String, String>>() {

                        @Override
                        public void onResponse(
                                Call<java.util.Map<String, String>> call,
                                Response<java.util.Map<String, String>> response
                        ) {

                            if (response.isSuccessful()
                                    && response.body() != null) {

                                String translatedText =
                                        response.body()
                                                .get("translatedText");

                                tvTranslatedSuggestion
                                        .setText(translatedText);

                                speak(translatedText);
                            }
                        }

                        @Override
                        public void onFailure(
                                Call<java.util.Map<String, String>> call,
                                Throwable t
                        ) {

                            tvTranslatedSuggestion.setText(
                                    "Translation failed"
                            );
                        }
                    });
        });
        btnRefreshSuggestions.setOnClickListener(v -> {


            String detectedWord =
                    tvDetectedWord.getText().toString().trim();

            if (detectedWord.isEmpty() ||
                    detectedWord.equalsIgnoreCase("Unknown")) {

                return;
            }

            ApiServiceSpring api =
                    RetrofitClientSpring
                            .getClient()
                            .create(ApiServiceSpring.class);

            SuggestionRequest request =
                    new SuggestionRequest(
                            detectedWord,
                            customModeName != null && !customModeName.isEmpty()
                                    ? customModeName
                                    : activeAppMode,
                            "english"
                    );

            api.getSuggestions(request)
                    .enqueue(new Callback<SuggestionResponse>() {

                        @Override
                        public void onResponse(
                                Call<SuggestionResponse> call,
                                Response<SuggestionResponse> response
                        ) {

                            Log.d("STEP_DEBUG", "1 response entered");

                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().getSuggestions() != null) {

                                Log.d("STEP_DEBUG", "2 response valid");

                                java.util.List<String> suggestions =
                                        response.body().getSuggestions();

                                Log.d("STEP_DEBUG", "3 suggestions = " + suggestions);

                                tvSuggestion1.setText(
                                        suggestions.size() > 0
                                                ? suggestions.get(0)
                                                : ""
                                );

                                tvSuggestion2.setText(
                                        suggestions.size() > 1
                                                ? suggestions.get(1)
                                                : ""
                                );

                                tvSuggestion3.setText(
                                        suggestions.size() > 2
                                                ? suggestions.get(2)
                                                : ""
                                );

                                Log.d("STEP_DEBUG", "4 textviews updated");

                            } else {

                                Log.d("STEP_DEBUG", "response invalid");

                            }
                        }
                        @Override
                        public void onFailure(
                                Call<SuggestionResponse> call,
                                Throwable t
                        ) {

                            tvSuggestion1.setText("No suggestions");
                            tvSuggestion2.setText("");
                            tvSuggestion3.setText("");
                        }
                    });
        });
    }private void setupLanguageSpinner() {

        String[] languages = {
                "Arabic",
                "French",
                "Spanish",
                "German",
                "Turkish"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        languages
                ) {

                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            android.view.ViewGroup parent
                    ) {

                        TextView view =
                                (TextView) super.getView(
                                        position,
                                        convertView,
                                        parent
                                );

                        view.setTextColor(
                                isDarkMode
                                        ? Color.WHITE
                                        : Color.BLACK
                        );

                        return view;
                    }

                    @Override
                    public View getDropDownView(
                            int position,
                            View convertView,
                            android.view.ViewGroup parent
                    ) {

                        TextView view =
                                (TextView) super.getDropDownView(
                                        position,
                                        convertView,
                                        parent
                                );

                        view.setTextColor(Color.BLACK);

                        return view;
                    }
                };

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerLanguage.setAdapter(adapter);
    }
    private void testBackendLogin() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        LoginRequest request = new LoginRequest(
                "hiba@test.com",
                "123456"
        );

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();
                    System.out.println("LOGIN_SUCCESS: " + user.getFullName());
                } else {
                    System.out.println("LOGIN_FAILED_CODE: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                System.out.println("LOGIN_ERROR: " + t.getMessage());
            }
        });
    }
    private void loginUser() {
        String email = etLoginEmail.getText().toString().trim();
        String password = etLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            tvLoginStatus.setText("Please enter email and password");
            return;
        }

        tvLoginStatus.setText("Logging in...");

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();
                    loggedInUserId = user.getUserId();
                    loggedInUserName = user.getFullName();
                    System.out.println("USER_ID_SAVED: " + loggedInUserId);
                    tvLoginStatus.setText("Welcome " + user.getFullName());
                    tvDetectedWord.setText("Logged In");
                    tvDetectedMeaning.setText("User ID: " + user.getUserId());

                    System.out.println("LOGIN_SUCCESS: " + user.getFullName());
                } else {
                    tvLoginStatus.setText("Login failed. Check email/password.");
                    System.out.println("LOGIN_FAILED_CODE: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                tvLoginStatus.setText("Connection error: " + t.getMessage());
                System.out.println("LOGIN_ERROR: " + t.getMessage());
            }
        });
    }
    private void setupSliders() {

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateManualData();
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
        seekAccX.setOnSeekBarChangeListener(simpleSeekListener);
        seekAccY.setOnSeekBarChangeListener(simpleSeekListener);
        seekAccZ.setOnSeekBarChangeListener(simpleSeekListener);
        seekGyroX.setOnSeekBarChangeListener(simpleSeekListener);
        seekGyroY.setOnSeekBarChangeListener(simpleSeekListener);
        seekGyroZ.setOnSeekBarChangeListener(simpleSeekListener);
    }

    private void setupChart() {
        thumbSet = createSet("Thumb", "#00E676");
        indexSet = createSet("Index", "#40C4FF");
        middleSet = createSet("Middle", "#FFD740");
        ringSet = createSet("Ring", "#B388FF");
        pinkySet = createSet("Pinky", "#FF4081");

        lineData = new LineData(thumbSet, indexSet, middleSet, ringSet, pinkySet);
        liveLineChart.setData(lineData);
        liveLineChart.getDescription().setEnabled(false);
        liveLineChart.setTouchEnabled(true);
        liveLineChart.setDragEnabled(true);
        liveLineChart.setScaleEnabled(true);

        XAxis xAxis = liveLineChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = liveLineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(4095f);

        liveLineChart.getAxisRight().setEnabled(false);
        liveLineChart.invalidate();
    }
    private String getOrientation(int accX, int accY, int accZ) {
        if (accZ > 10000) {
            return "PALM UP";
        } else if (accZ < -10000) {
            return "PALM DOWN";
        } else if (accX > 10000) {
            return "LEFT";
        } else if (accX < -10000) {
            return "RIGHT";
        } else {
            return "FLAT";
        }
    }
    private void updateMode(long modeId) {
        if (loggedInUserId == -1) return;

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        ActiveModeRequest request = new ActiveModeRequest(loggedInUserId, modeId);

        apiService.setActiveMode(request).enqueue(new Callback<ModeResponse>() {
            @Override
            public void onResponse(Call<ModeResponse> call, Response<ModeResponse> response) {
                if (response.isSuccessful()) {
                    System.out.println("MODE_UPDATED: " + modeId);
                } else {
                    System.out.println("MODE_FAILED");
                }
            }

            @Override
            public void onFailure(Call<ModeResponse> call, Throwable t) {
                System.out.println("MODE_ERROR: " + t.getMessage());
            }
        });
    }
    private final SeekBar.OnSeekBarChangeListener simpleSeekListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    updateManualData();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };
    private LineDataSet createSet(String label, String color) {
        LineDataSet set = new LineDataSet(new ArrayList<>(), label);
        set.setColor(Color.parseColor(color));
        set.setLineWidth(2.5f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        return set;
    }

    private void updateChart(int thumb, int index, int middle, int ring, int pinky) {
        lineData.addEntry(new Entry(timeIndex, thumb), 0);
        lineData.addEntry(new Entry(timeIndex, index), 1);
        lineData.addEntry(new Entry(timeIndex, middle), 2);
        lineData.addEntry(new Entry(timeIndex, ring), 3);
        lineData.addEntry(new Entry(timeIndex, pinky), 4);

        lineData.notifyDataChanged();
        liveLineChart.notifyDataSetChanged();
        liveLineChart.setVisibleXRangeMaximum(20);
        liveLineChart.moveViewToX(timeIndex);
        liveLineChart.invalidate();

        timeIndex++;
    }

    private void showManualMode() {

        isManualMode = true;

        // STOP WOKWI COMPLETELY
        isSimulationRunning = false;
        handler.removeCallbacks(liveRunnable);

        manualPanel.setVisibility(View.VISIBLE);
        simulationPanel.setVisibility(View.GONE);

        tvConnectionStatus.setText(
                "Manual sliders active — no hardware required"
        );

        refreshButtons();

        updateManualData();
    }

    private void showSimulationMode() {
        isManualMode = false;

        manualPanel.setVisibility(View.GONE);
        simulationPanel.setVisibility(View.VISIBLE);

        tvConnectionStatus.setText("Connecting to Wokwi stream...");

        refreshButtons();

       // isSimulationRunning = true;
      //  handler.removeCallbacks(liveRunnable);
      //  handler.post(liveRunnable);
        isSimulationRunning = true;
        handler.removeCallbacks(liveRunnable);
        handler.postDelayed(liveRunnable, 300);
    }

    private void updateManualData() {
        int thumb = seekThumb.getProgress() * 40;
        int index = seekIndex.getProgress() * 40;
        int middle = seekMiddle.getProgress() * 40;
        int ring = seekRing.getProgress() * 40;
        int pinky = seekPinky.getProgress() * 40;
        updateFingerStates(thumb, index, middle, ring, pinky, 0);
        tvThumbValue.setText("Thumb: " + seekThumb.getProgress() + "%");
        tvIndexValue.setText("Index: " + seekIndex.getProgress() + "%");
        tvMiddleValue.setText("Middle: " + seekMiddle.getProgress() + "%");
        tvRingValue.setText("Ring: " + seekRing.getProgress() + "%");
        tvPinkyValue.setText("Pinky: " + seekPinky.getProgress() + "%");
        int accX = seekAccX.getProgress() - 50;
        int accY = seekAccY.getProgress() - 50;
        int accZ = seekAccZ.getProgress() - 50;
        tvManualAccX.setText("Acc X: " + accX);
        tvManualAccY.setText("Acc Y: " + accY);
        tvManualAccZ.setText("Acc Z: " + accZ);

        tvAccValues.setText("ACC: X=" + accX + " Y=" + accY + " Z=" + accZ);

        String orientation = getOrientation(accX, accY, accZ);
        tvHandOrientation.setText("Hand Orientation: " + orientation);
        int gyroX = seekGyroX.getProgress() - 50;
        int gyroY = seekGyroY.getProgress() - 50;
        int gyroZ = seekGyroZ.getProgress() - 50;
        tvManualGyroX.setText("Gyro X: " + gyroX);
        tvManualGyroY.setText("Gyro Y: " + gyroY);
        tvManualGyroZ.setText("Gyro Z: " + gyroZ);

        tvGyroValues.setText("GYRO: X=" + gyroX + " Y=" + gyroY + " Z=" + gyroZ);

    }

    private void fetchWokwiData() {
        if (isWokwiFetchInProgress) {
            return;
        }

        isWokwiFetchInProgress = true;

        new Thread(() -> {
            try {
                URL url = new URL(WOKWI_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();
                connection.disconnect();
                isWokwiFetchInProgress = false;
                String data = response.toString().trim();
                ParsedData parsed = parseData(data);

                runOnUiThread(() -> {

                    if (isManualMode || !isSimulationRunning) {
                        return;
                    }

                    wokwiFailCount = 0;
                    tvConnectionStatus.setText("Connected to Wokwi stream");
                    tvLiveJson.setText(displayData(data));
                    tvAccValues.setText("ACC: X=" + parsed.accX +
                            " Y=" + parsed.accY +
                            " Z=" + parsed.accZ);

                    tvGyroValues.setText("GYRO: X=" + parsed.gyroX +
                            " Y=" + parsed.gyroY +
                            " Z=" + parsed.gyroZ);

                    updateFingerStates(
                            parsed.thumb,
                            parsed.index,
                            parsed.middle,
                            parsed.ring,
                            parsed.pinky,
                            parsed.sos
                    );

                    updateChart(
                            parsed.thumb,
                            parsed.index,
                            parsed.middle,
                            parsed.ring,
                            parsed.pinky
                    );
                    if (isSimulationRunning) {
                        updateImuChart(
                                parsed.accX,
                                parsed.accY,
                                parsed.accZ,
                                parsed.gyroX,
                                parsed.gyroY,
                                parsed.gyroZ
                        );
                    }
                });

            } catch (Exception e) {
                isWokwiFetchInProgress = false;
                runOnUiThread(() -> {

                    if (isManualMode || !isSimulationRunning) {
                        return;
                    }

                    wokwiFailCount++;

                    if (wokwiFailCount >= 3) {
                        tvConnectionStatus.setText("Connection failed");
                        tvLiveJson.setText("Error: " + e.getMessage());
                    } else {
                        tvConnectionStatus.setText("Connected to Wokwi stream");
                    }
                });
            }
        }).start();
    }

    private ParsedData parseData(String data) throws Exception {
        ParsedData p = new ParsedData();

        if (data.startsWith("{")) {
            JSONObject json = new JSONObject(data);
            p.thumb = json.getInt("thumb");
            p.index = json.getInt("index");
            p.middle = json.getInt("middle");
            p.ring = json.getInt("ring");
            p.pinky = json.getInt("pinky");
            p.sos = json.has("sos") ? json.getInt("sos") : 0;
            p.accX = json.getInt("accX");
            p.accY = json.getInt("accY");
            p.accZ = json.getInt("accZ");

            p.gyroX = json.getInt("gyroX");
            p.gyroY = json.getInt("gyroY");
            p.gyroZ = json.getInt("gyroZ");
        } else {
            String[] v = data.split(",");

            p.thumb = Integer.parseInt(v[0].trim());
            p.index = Integer.parseInt(v[1].trim());
            p.middle = Integer.parseInt(v[2].trim());
            p.ring = Integer.parseInt(v[3].trim());
            p.pinky = Integer.parseInt(v[4].trim());

            p.accX = Integer.parseInt(v[5].trim());
            p.accY = Integer.parseInt(v[6].trim());
            p.accZ = Integer.parseInt(v[7].trim());

            p.gyroX = Integer.parseInt(v[8].trim());
            p.gyroY = Integer.parseInt(v[9].trim());
            p.gyroZ = Integer.parseInt(v[10].trim());

            p.sos = v.length > 11 ? Integer.parseInt(v[11].trim()) : 0;
            Log.d("SOS_CHECK", "SOS VALUE = " + p.sos);
            Log.d("PARSE_CHECK", "thumb=" + p.thumb +
                    " index=" + p.index +
                    " middle=" + p.middle +
                    " ring=" + p.ring +
                    " pinky=" + p.pinky);
        }

        return p;
    }

    private String displayData(String data) {
        try {
            if (data.trim().startsWith("{")) {
                return new JSONObject(data).toString(4);
            }

            // fallback CSV
            String[] v = data.split(",");
            return "CSV Stream\n\n"
                    + "thumb: " + v[0] + "\n"
                    + "index: " + v[1] + "\n"
                    + "middle: " + v[2] + "\n"
                    + "ring: " + v[3] + "\n"
                    + "pinky: " + v[4] + "\n"
                    + "accX: " + v[5] + "\n"
                    + "accY: " + v[6] + "\n"
                    + "accZ: " + v[7] + "\n"
                    + "gyroX: " + v[8] + "\n"
                    + "gyroY: " + v[9] + "\n"
                    + "gyroZ: " + v[10];
        } catch (Exception e) {
            return data;
        }
    }

    private void updateFingerStates(int thumb, int index, int middle, int ring, int pinky, int sos) {
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

        currentPatternKey = t + "," + i + "," + m + "," + r + "," + p;

        long now = System.currentTimeMillis();

        if (!currentPatternKey.equals(pendingPatternKey)) {
            pendingPatternKey = currentPatternKey;
            patternStableStart = now;
            return;
        }

        if (now - patternStableStart < PATTERN_STABLE_TIME) {
            return;
        }

        tvPattern.setText("Pattern: " + currentPatternKey);
        tvCurrentPattern.setText("Current pattern: " + currentPatternKey);

        if (handAnimationView != null) {
            handAnimationView.updateFingers(thumb, index, middle, ring, pinky, currentPatternKey);
        }


        if (sos == 1 && !sosPageOpened) {

            sosPageOpened = true;

            runOnUiThread(() -> {
                Intent intent = new Intent(MainActivity.this, SOSActivity.class);
                startActivity(intent);
            });

            return;
        }

        if (sos == 0) {
            sosPageOpened = false;
        }
        if (!isFirstLoad) {

            int accX;
            int accY;
            int accZ;

            int gyroX;
            int gyroY;
            int gyroZ;

            if (isManualMode) {

                accX = seekAccX.getProgress() - 50;
                accY = seekAccY.getProgress() - 50;
                accZ = seekAccZ.getProgress() - 50;

                gyroX = seekGyroX.getProgress() - 50;
                gyroY = seekGyroY.getProgress() - 50;
                gyroZ = seekGyroZ.getProgress() - 50;

            } else {

                accX = Integer.parseInt(tvAccValues.getText().toString()
                        .split("X=")[1]
                        .split(" ")[0]);

                accY = Integer.parseInt(tvAccValues.getText().toString()
                        .split("Y=")[1]
                        .split(" ")[0]);

                accZ = Integer.parseInt(tvAccValues.getText().toString()
                        .split("Z=")[1]);

                gyroX = Integer.parseInt(tvGyroValues.getText().toString()
                        .split("X=")[1]
                        .split(" ")[0]);

                gyroY = Integer.parseInt(tvGyroValues.getText().toString()
                        .split("Y=")[1]
                        .split(" ")[0]);

                gyroZ = Integer.parseInt(tvGyroValues.getText().toString()
                        .split("Z=")[1]);
            }
            now = System.currentTimeMillis();

            if (now - lastSendTime < 2500) {
                return;
            }

            lastSentPattern = currentPatternKey;
            lastSendTime = now;
            sendGestureToBackend(
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
        }
    }
    private String state(int value) {
        if (value < 1300)
            return "OPEN";
        else if (value < 2700)
            return "HALF";
        else
            return "CLOSED";
    }

    private void addToHistory(String word, String meaning) {
        if (historyList == null) return;
        if (word.equals("Unknown")) return;

        String entry = word + " — " + meaning;

        if (entry.equals(lastHistoryEntry)) return;
        lastHistoryEntry = entry;

        TextView item = new TextView(this);
        item.setText("• " + entry);
        item.setTextSize(14);
        item.setPadding(0, 8, 0, 8);
        item.setTextColor(isDarkMode ? Color.WHITE : Color.parseColor("#111111"));

        historyList.addView(item, 0);

        if (historyList.getChildCount() > 8) {
            historyList.removeViewAt(historyList.getChildCount() - 1);
        }
    }

    private void applyTheme() {

        int bg = isDarkMode ? Color.parseColor("#0D0F1A") : Color.parseColor("#F5F6FA");
        int card = isDarkMode ? Color.parseColor("#1A1D2E") : Color.WHITE;
        int mini = isDarkMode ? Color.parseColor("#24283A") : Color.parseColor("#EEF1F8");
        int stroke = isDarkMode ? Color.parseColor("#2B2F45") : Color.parseColor("#DDE1EE");
        int miniStroke = isDarkMode ? Color.parseColor("#383D5C") : Color.parseColor("#D8DDED");
        int primary = isDarkMode ? Color.WHITE : Color.parseColor("#111111");
        int secondary = isDarkMode ? Color.parseColor("#A0A3BD") : Color.parseColor("#666666");

        if (isDarkMode) {
            imgPeople.setImageResource(R.drawable.people_dark);
        } else {
            imgPeople.setImageResource(R.drawable.people_light);
        }

        if (isDarkMode) {
            tvGreeting.setTextColor(Color.WHITE);
            tvUserName.setTextColor(Color.WHITE);
        } else {
            tvGreeting.setTextColor(Color.BLACK);
            tvUserName.setTextColor(Color.BLACK);
        }

        rootScroll.setBackgroundColor(bg);

        statusCard.setBackground(round(card, stroke, 22));
        resultCard.setBackground(round(card, stroke, 22));
        fingerStatesCard.setBackground(round(card, stroke, 22));

        if (handAnimationCard != null)
            handAnimationCard.setBackground(round(card, stroke, 22));

        if (handAnimationView != null) {
            handAnimationView.setDarkMode(isDarkMode);
            handAnimationView.setBackground(round(mini, miniStroke, 20));
        }

        manualPanel.setBackground(round(card, stroke, 22));
        simulationPanel.setBackground(round(card, stroke, 22));
        customCard.setBackground(round(card, stroke, 22));
        startCard.setBackground(round(card, stroke, 22));

        tvStartTitle.setTextColor(primary);
        tvStartSubtitle.setTextColor(secondary);

        btnStartNow.setTextColor(Color.WHITE);
        btnStartNow.setBackground(
                round(Color.parseColor("#7B61FF"),
                        Color.parseColor("#7B61FF"), 18));

        cardThumb.setBackground(round(mini, miniStroke, 16));
        cardIndex.setBackground(round(mini, miniStroke, 16));
        cardMiddle.setBackground(round(mini, miniStroke, 16));
        cardRing.setBackground(round(mini, miniStroke, 16));
        cardPinky.setBackground(round(mini, miniStroke, 16));

        tvStatusLabel.setTextColor(secondary);
        tvConnectionStatus.setTextColor(secondary);

        tvResultLabel.setTextColor(secondary);
        tvDetectedMeaning.setTextColor(primary);
        suggestionsContainer.setBackground(round(mini, miniStroke, 16));

        tvSuggestion1.setTextColor(primary);
        tvSuggestion2.setTextColor(primary);
        tvSuggestion3.setTextColor(primary);
        tvTranslatedSuggestion.setTextColor(primary);

        tvTranslatedSuggestion.setBackground(
                round(mini, miniStroke, 16)
        );

        tvSuggestion1.setBackgroundColor(Color.TRANSPARENT);
        tvSuggestion2.setBackgroundColor(Color.TRANSPARENT);
        tvSuggestion3.setBackgroundColor(Color.TRANSPARENT);
       // tvFutureSentence.setTextColor(secondary);
       // tvFutureSentence.setBackground(round(mini, miniStroke, 16));

        tvFingerTitle.setTextColor(primary);
        tvPattern.setTextColor(secondary);

        if (tvHandAnimationTitle != null)
            tvHandAnimationTitle.setTextColor(primary);

        if (tvHandAnimationSubtitle != null)
            tvHandAnimationSubtitle.setTextColor(secondary);

        tvThumbLabel.setTextColor(secondary);
        tvIndexLabel.setTextColor(secondary);
        tvMiddleLabel.setTextColor(secondary);
        tvRingLabel.setTextColor(secondary);
        tvPinkyLabel.setTextColor(secondary);

        tvManualTitle.setTextColor(primary);
        tvManualSubtitle.setTextColor(secondary);

        tvSimulationTitle.setTextColor(primary);
        tvSimulationSubtitle.setTextColor(secondary);

        tvLiveJson.setTextColor(primary);
        tvLiveJson.setBackground(round(mini, miniStroke, 16));

        tvChartTitle.setTextColor(primary);

        tvThumbValue.setTextColor(primary);
        tvIndexValue.setTextColor(primary);
        tvMiddleValue.setTextColor(primary);
        tvRingValue.setTextColor(primary);
        tvPinkyValue.setTextColor(primary);

        // =========================
        // FIX FOR LIGHT MODE TEXT
        // =========================

        tvManualAccX.setTextColor(primary);
        tvManualAccY.setTextColor(primary);
        tvManualAccZ.setTextColor(primary);

        tvManualGyroX.setTextColor(primary);
        tvManualGyroY.setTextColor(primary);
        tvManualGyroZ.setTextColor(primary);

        tvAccValues.setTextColor(primary);
        tvGyroValues.setTextColor(primary);
        tvHandOrientation.setTextColor(primary);

        // =========================

        tvCustomTitle.setTextColor(primary);
        tvCurrentPattern.setTextColor(secondary);

        etCustomWord.setTextColor(primary);
        etCustomWord.setHintTextColor(secondary);
        etCustomWord.setBackground(round(mini, miniStroke, 16));

        tvHistoryTitle.setTextColor(primary);

        for (int i = 0; i < historyList.getChildCount(); i++) {
            TextView item = (TextView) historyList.getChildAt(i);
            item.setTextColor(primary);
        }

        btnThemeToggle.setText(isDarkMode ? "☾" : "☀");
        btnThemeToggle.setTextColor(primary);
        btnThemeToggle.setBackground(round(mini, miniStroke, 18));

        liveLineChart.setBackground(round(mini, miniStroke, 16));
        liveLineChart.getLegend().setTextColor(primary);
        liveLineChart.getAxisLeft().setTextColor(secondary);
        liveLineChart.getAxisLeft().setGridColor(miniStroke);
        liveLineChart.invalidate();

        imuLineChart.setBackground(round(mini, miniStroke, 16));
        imuLineChart.getLegend().setTextColor(primary);
        imuLineChart.getAxisLeft().setTextColor(secondary);
        imuLineChart.getAxisLeft().setGridColor(miniStroke);
        imuLineChart.getXAxis().setTextColor(secondary);
        imuLineChart.getXAxis().setGridColor(miniStroke);
        imuLineChart.invalidate();

        btnSOS.setTextColor(Color.WHITE);
        btnSOS.setBackground(
                round(Color.parseColor("#D50000"),
                        Color.parseColor("#D50000"), 18));

      /*  btnMockPacket.setTextColor(Color.WHITE);
        btnMockPacket.setBackground(
                round(Color.parseColor("#7B61FF"),
                        Color.parseColor("#7B61FF"), 18));*/

        btnSaveGesture.setTextColor(Color.WHITE);
        btnSaveGesture.setBackground(
                round(Color.parseColor("#7B61FF"),
                        Color.parseColor("#7B61FF"), 18));

        refreshButtons();
        refreshAppModeButtons();

    /*
    loginCard.setBackground(round(card, stroke, 22));

    tvLoginTitle.setTextColor(primary);
    tvLoginStatus.setTextColor(secondary);

    etLoginEmail.setTextColor(primary);
    etLoginEmail.setHintTextColor(secondary);
    etLoginEmail.setBackground(round(mini, miniStroke, 16));

    etLoginPassword.setTextColor(primary);
    etLoginPassword.setHintTextColor(secondary);
    etLoginPassword.setBackground(round(mini, miniStroke, 16));

    btnLogin.setTextColor(Color.WHITE);
    btnLogin.setBackground(
            round(Color.parseColor("#7B61FF"),
                    Color.parseColor("#7B61FF"), 18));
    */
    }

    private void refreshButtons() {
        int inactive = isDarkMode ? Color.parseColor("#24283A") : Color.parseColor("#EEF1F8");
        int inactiveStroke = isDarkMode ? Color.parseColor("#383D5C") : Color.parseColor("#D8DDED");
        int inactiveText = isDarkMode ? Color.WHITE : Color.parseColor("#111111");

        if (isManualMode) {
            btnManualMode.setBackground(round(Color.parseColor("#7B61FF"), Color.parseColor("#7B61FF"), 18));
            btnSimulationMode.setBackground(round(inactive, inactiveStroke, 18));
            btnManualMode.setTextColor(Color.WHITE);
            btnSimulationMode.setTextColor(inactiveText);
        } else {
            btnSimulationMode.setBackground(round(Color.parseColor("#7B61FF"), Color.parseColor("#7B61FF"), 18));
            btnManualMode.setBackground(round(inactive, inactiveStroke, 18));
            btnSimulationMode.setTextColor(Color.WHITE);
            btnManualMode.setTextColor(inactiveText);
        }
    }

    private void setAppMode(String mode) {
        activeAppMode = mode;
        tvCurrentMode.setText(mode + " Mode");

        resetDetectedResult();

        if (mode.equals("Emergency")) {
            tvConnectionStatus.setText("Emergency gestures are prioritized.");
        } else if (mode.equals("Health")) {
            tvConnectionStatus.setText("Health communication mode active.");
        } else {
            tvConnectionStatus.setText(isManualMode
                    ? "Manual sliders active — no hardware required"
                    : "Wokwi simulation stream active");
        }

        refreshAppModeButtons();
    }
    private void resetDetectedResult() {
        tvDetectedWord.setText("Unknown");
        tvDetectedMeaning.setText("Unknown gesture");
        //  tvFutureSentence.setText("AI sentence will appear here later");
        lastMeaning = "";
    }
    private void addImuEntry(LineData data, float value, String label) {

        LineDataSet set = null;

        for (ILineDataSet iSet : data.getDataSets()) {
            if (iSet.getLabel().equals(label)) {
                set = (LineDataSet) iSet;
                break;
            }
        }

        if (set == null) {
            set = new LineDataSet(new ArrayList<>(), label);

            if (label.equals("Acc X")) set.setColor(Color.parseColor("#00E676"));
            else if (label.equals("Acc Y")) set.setColor(Color.parseColor("#40C4FF"));
            else if (label.equals("Acc Z")) set.setColor(Color.parseColor("#FFD740"));
            else if (label.equals("Gyro X")) set.setColor(Color.parseColor("#B388FF"));
            else if (label.equals("Gyro Y")) set.setColor(Color.parseColor("#FF4081"));
            else if (label.equals("Gyro Z")) set.setColor(Color.parseColor("#FF9100"));

            set.setDrawCircles(false);
            set.setDrawValues(false);
            set.setLineWidth(2.5f);
            set.setMode(LineDataSet.Mode.LINEAR);

            data.addDataSet(set);
        }

        data.addEntry(new Entry(set.getEntryCount(), value), data.getIndexOfDataSet(set));
    }
    private void setupImuChart() {
        imuLineChart.getDescription().setEnabled(false);
        imuLineChart.setTouchEnabled(true);
        imuLineChart.setDragEnabled(true);
        imuLineChart.setScaleEnabled(true);
        imuLineChart.setBackgroundColor(Color.TRANSPARENT);

        XAxis xAxis = imuLineChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setDrawGridLines(false);

        YAxis yAxis = imuLineChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setAxisMinimum(-20000f);
        yAxis.setAxisMaximum(20000f);

        imuLineChart.getAxisRight().setEnabled(false);
        imuLineChart.invalidate();
    }
    private void updateImuChart(float accX, float accY, float accZ,
                                float gyroX, float gyroY, float gyroZ) {

        LineData data = imuLineChart.getData();

        if (data == null) {
            data = new LineData();
            imuLineChart.setData(data);
        }

        addImuEntry(data, accX, "Acc X");
        addImuEntry(data, accY, "Acc Y");
        addImuEntry(data, accZ, "Acc Z");
        addImuEntry(data, gyroX, "Gyro X");
        addImuEntry(data, gyroY, "Gyro Y");
        addImuEntry(data, gyroZ, "Gyro Z");

        imuLineChart.notifyDataSetChanged();
        imuLineChart.invalidate();
    }

    private void refreshAppModeButtons() {
        int active = Color.parseColor("#7B61FF");
        int inactive = isDarkMode ? Color.parseColor("#24283A") : Color.parseColor("#EEF1F8");
        int inactiveStroke = isDarkMode ? Color.parseColor("#383D5C") : Color.parseColor("#D8DDED");

        if (activeAppMode.equals("Default")) {
            modeHome.setBackground(round(active, active, 18));
            modeEmergency.setBackground(round(inactive, inactiveStroke, 18));
            modeHealth.setBackground(round(inactive, inactiveStroke, 18));
        } else if (activeAppMode.equals("Emergency")) {
            modeEmergency.setBackground(round(Color.parseColor("#D50000"), Color.parseColor("#D50000"), 18));
            modeHome.setBackground(round(inactive, inactiveStroke, 18));
            modeHealth.setBackground(round(inactive, inactiveStroke, 18));
        } else {
            modeHealth.setBackground(round(active, active, 18));
            modeHome.setBackground(round(inactive, inactiveStroke, 18));
            modeEmergency.setBackground(round(inactive, inactiveStroke, 18));
        }

        modeHome.setTextColor(Color.WHITE);
        modeEmergency.setTextColor(Color.WHITE);
        modeHealth.setTextColor(Color.WHITE);
    }

    private GradientDrawable round(int color, int strokeColor, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(radiusDp * getResources().getDisplayMetrics().density);
        d.setStroke(1, strokeColor);
        return d;
    }

    static class ParsedData {
        int thumb, index, middle, ring, pinky, sos;
        int accX;
        int accY;
        int accZ;
        int gyroX;
        int gyroY;
        int gyroZ;
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

            if (savedName != null
                    && !savedName.trim().isEmpty()
                    && !savedName.equals("Guest")) {

                profileIcon.setText(
                        savedName.substring(0, 1).toUpperCase()
                );

            } else {

                profileIcon.setText(
                        savedEmail.substring(0, 1).toUpperCase()
                );
            }

        } else if (savedName != null
                && !savedName.trim().isEmpty()
                && !savedName.equals("Guest")) {

            tvUserName.setText(savedName);

            profileIcon.setText(
                    savedName.substring(0, 1).toUpperCase()
            );

        } else {

            tvUserName.setText("Guest");

            profileIcon.setText("👤");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        setupUserProfile();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isSimulationRunning = false;
        handler.removeCallbacks(liveRunnable);
    }
    private void sendTestGesture() {
        if (loggedInUserId == -1) return;

        ApiServiceSpring api = RetrofitClientSpring
                .getClient()
                .create(ApiServiceSpring.class);
        // modeId: 1=Home, 2=Emergency, 3=Health
        long modeId = 2; // جربي Emergency

        int accX = seekAccX.getProgress() - 50;
        int accY = seekAccY.getProgress() - 50;
        int accZ = seekAccZ.getProgress() - 50;

        int gyroX = seekGyroX.getProgress() - 50;
        int gyroY = seekGyroY.getProgress() - 50;
        int gyroZ = seekGyroZ.getProgress() - 50;

        int thumb = seekThumb.getProgress() * 40;
        int index = seekIndex.getProgress() * 40;
        int middle = seekMiddle.getProgress() * 40;
        int ring = seekRing.getProgress() * 40;
        int pinky = seekPinky.getProgress() * 40;

        SensorDataRequest request = new SensorDataRequest(
                loggedInUserId,
                currentModeId,
                thumb,
                index,
                middle,
                ring,
                pinky,
                false,
                accX,
                accY,
                accZ,
                gyroX,
                gyroY,
                gyroZ
        );
        api.translate(request).enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TranslationResponse res = response.body();

                    System.out.println("RESULT: " + res.getMeaning());

                    tvDetectedWord.setText(res.getMeaning());
                    tvDetectedMeaning.setText(res.getGesture());
                } else {
                    System.out.println("FAILED");
                }
            }

            @Override
            public void onFailure(Call<TranslationResponse> call, Throwable t) {
                System.out.println("ERROR: " + t.getMessage());
            }
        });
    }

    private void sendGestureToBackend(
            int thumb,
            int index,
            int middle,
            int ring,
            int pinky,
            int accX,
            int accY,
            int accZ,
            int gyroX,
            int gyroY,
            int gyroZ
    ) {
        if (loggedInUserId == -1) return;
        if (isTranslateInProgress) {
            Log.d("TIME_CHECK", "Skipped: translate still in progress");
            return;
        }

        isTranslateInProgress = true;


        ApiServiceSpring api = RetrofitClientSpring
                .getClient()
                .create(ApiServiceSpring.class);



        SensorDataRequest request = new SensorDataRequest(
                loggedInUserId,
                currentModeId,
                thumb,
                index,
                middle,
                ring,
                pinky,
                false,
                accX,
                accY,
                accZ,
                gyroX,
                gyroY,
                gyroZ
        );

        // Clean log (main)
      //  Log.d("TRANSLATION", "SENDING modeId=" + currentModeId);
        Log.d("TRANSLATE_DEBUG", "USER ID = " + loggedInUserId);
        Log.d("TRANSLATE_DEBUG", "MODE ID = " + currentModeId);
        Log.d("TRANSLATE_DEBUG", "MODE NAME = " + activeAppMode);
        Log.d("TRANSLATE_DEBUG", "PATTERN = " + currentPatternKey);
        Log.d("TRANSLATE_DEBUG", "ACC = " + accX + "," + accY + "," + accZ);
        Log.d("TRANSLATE_DEBUG", "GYRO = " + gyroX + "," + gyroY + "," + gyroZ);
        // Debug (keep for later if needed)
        // Log.d("TRANSLATION", "SENDING REQUEST modeId=" + currentModeId);
        long translateStart = System.currentTimeMillis();
        Log.d("TIME_CHECK", "Translate request started");
        api.translate(request).enqueue(new Callback<TranslationResponse>() {
            @Override
            public void onResponse(Call<TranslationResponse> call, Response<TranslationResponse> response) {
                isTranslateInProgress = false;
                Log.d("TIME_CHECK", "Translate response took: " +
                        (System.currentTimeMillis() - translateStart) + " ms");
                if (response.isSuccessful() && response.body() != null) {
                    TranslationResponse res = response.body();

                    tvDetectedWord.setText(res.getGesture());
                    tvDetectedMeaning.setText(res.getMeaning());
                    lastMeaning = res.getMeaning(); // 👈 هون ضيفيها

                  //  int currentSuggestionRequestId = ++suggestionRequestId;
                    String currentDetectedGesture = res.getGesture();
                    if (currentDetectedGesture == null
                            || currentDetectedGesture.equalsIgnoreCase("UNKNOWN_GESTURE")
                            || currentDetectedGesture.equalsIgnoreCase("Unknown")) {

                        Log.d("AI_DEBUG", "Skip AI because gesture is unknown");
                        return;
                    }
                    long nowAi = System.currentTimeMillis();

                    if (currentDetectedGesture.equals(lastAiGesture)
                            ) {

                        Log.d("AI_COOLDOWN", "Skipped duplicate AI request");

                        String suggestionKey = currentModeId + ":" + currentDetectedGesture;

                        if (suggestionsCache.containsKey(suggestionKey)) {
                            showSuggestions(suggestionsCache.get(suggestionKey));
                        }

                        return;
                    }
                    String suggestionKey =
                            currentModeId + ":" + currentDetectedGesture;

                    if (suggestionKey.equals(lastSuggestionKey)
                            && suggestionsCache.containsKey(suggestionKey)) {

                        showSuggestions(suggestionsCache.get(suggestionKey));
                        return;
                    }

                 //   lastSuggestionKey = suggestionKey;

                    SuggestionRequest suggestionRequest =
                            new SuggestionRequest(
                                    currentDetectedGesture,
                                    customModeName != null && !customModeName.isEmpty()
                                            ? customModeName
                                            : activeAppMode,
                                    "english"
                            );
                   long aiStart = System.currentTimeMillis();
                    if (isAiSuggestionInProgress) {
                        return;
                    }

                    isAiSuggestionInProgress = true;
                    int currentSuggestionRequestId = ++suggestionRequestId;
                    Log.d("TIME_CHECK", "AI request started");
                    tvSuggestion1.setText("Loading suggestions...");
                    tvSuggestion2.setText("");
                    tvSuggestion3.setText("");
                    api.getSuggestions(suggestionRequest)
                            .enqueue(new Callback<SuggestionResponse>() {

                                @Override
                                public void onResponse(
                                        Call<SuggestionResponse> call,
                                        Response<SuggestionResponse> response
                                ) {
                                    isAiSuggestionInProgress = false;

                                    Log.d("SHOW_DEBUG", "response code = " + response.code());

                                    if (response.body() != null) {
                                        Log.d("SHOW_DEBUG", "suggestions = " + response.body().getSuggestions());
                                    } else {
                                        Log.d("SHOW_DEBUG", "BODY IS NULL");
                                    }

                                    if (response.isSuccessful()
                                            && response.body() != null
                                            && response.body().getSuggestions() != null
                                            && !response.body().getSuggestions().isEmpty()) {

                                        java.util.List<String> suggestions =
                                                response.body().getSuggestions();
                                        suggestionsCache.put(suggestionKey, suggestions);
                                        showSuggestions(suggestions);

                                        lastSuggestionKey = suggestionKey;
                                        lastAiGesture = currentDetectedGesture;
                                        lastAiTime = System.currentTimeMillis();
                                    }
                                }

                                @Override
                                public void onFailure(Call<SuggestionResponse> call, Throwable t) {
                                    isAiSuggestionInProgress = false;

                                    Log.d("SHOW_DEBUG", "AI FAILED = " + t.getMessage());

                                    tvSuggestion1.setText("AI is still generating, try again");
                                    tvSuggestion2.setText("");
                                    tvSuggestion3.setText("");
                                }
                            });
                    Log.d("TRANSLATION", "RESULT: " + res.getGesture());
                } else {
                    try {
                        Log.d("TRANSLATION", "FAILED: " + response.code() + " BODY: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.d("TRANSLATION", "FAILED: " + response.code());
                    }
                }
            }


      /*      private void addImuEntry(LineData data, float value, String label) {

                LineDataSet set = null;

                for (ILineDataSet iSet : data.getDataSets()) {
                    if (iSet.getLabel().equals(label)) {
                        set = (LineDataSet) iSet;
                        break;
                    }
                }

                if (set == null) {
                    set = new LineDataSet(new ArrayList<>(), label);
                    data.addDataSet(set);
                }

                data.addEntry(new Entry(set.getEntryCount(), value), data.getIndexOfDataSet(set));
            }*/

            @Override
            public void onFailure(Call<TranslationResponse> call, Throwable t) {
                isTranslateInProgress = false;
                Log.d("TRANSLATION", "ERROR: " + t.getMessage());
            }

        });

    }
    private void showSuggestions(
            java.util.List<String> suggestions

    ) {
        Log.d("SHOW_DEBUG", "showSuggestions called");
        tvSuggestion1.setText(
                suggestions.size() > 0 ? suggestions.get(0) : ""

        );   Log.d("SHOW_DEBUG", "S1 = " + suggestions.get(0));

        tvSuggestion2.setText(
                suggestions.size() > 1 ? suggestions.get(1) : ""
        );

        tvSuggestion3.setText(
                suggestions.size() > 2 ? suggestions.get(2) : ""
        );

        tvSuggestion1.setOnClickListener(v -> {
            selectedSuggestion = tvSuggestion1.getText().toString();
            speak(selectedSuggestion);
        });

        tvSuggestion2.setOnClickListener(v -> {
            selectedSuggestion = tvSuggestion2.getText().toString();
            speak(selectedSuggestion);
        });

        tvSuggestion3.setOnClickListener(v -> {
            selectedSuggestion = tvSuggestion3.getText().toString();
            speak(selectedSuggestion);
        });
    }
}