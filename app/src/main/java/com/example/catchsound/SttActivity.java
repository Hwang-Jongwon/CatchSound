package com.example.catchsound;


import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Dimension;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class SttActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Intent intent;
    SpeechRecognizer mRecognizer;
    Button sttBtn;
    TextView textView;
    final int PERMISSION = 1;
    FloatingActionButton fab_plus;
    FloatingActionButton fab_minus;

    public static Toast mToast;

    FloatingActionButton change1;


    FloatingActionButton fab_sub1;
    FloatingActionButton fab_sub2;
    FloatingActionButton fab_sub3;

    TextView tv1_tts;
    TextView tv1_db;
    TextView tv1_pron;

    private Animation fab_open, fab_close;

    boolean isopen = false;
    Button voice_set;

    private long time = 0;

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() - time >= 2000) {
            time = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else if (System.currentTimeMillis() - time < 2000) {

            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stt);
        ActionBar ab = getSupportActionBar();
        ab.hide();

        SharedPreferences pref = getSharedPreferences("checkFirst", Activity.MODE_PRIVATE);
        boolean checkFirst = pref.getBoolean("checkFirst", false);

        // false일 경우 최초 실행
        if (!checkFirst) {
            // 앱 최초 실행시 하고 싶은 작업
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("checkFirst", true);
            editor.apply();
            finish();

            Intent intent = new Intent(SttActivity.this, TutorialActivity.class);
            startActivity(intent);

        }


        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        editor = pref.edit();

        String guide = pref.getString("guide", "안녕하세요. 저는 청각장애가 있습니다. 버튼을 누르시고 핸드폰에 말해주시면 글로 볼 수 있습니다");

        textView = (TextView) findViewById(R.id.sttResult);
        textView.setText(guide);
        sttBtn = (Button) findViewById(R.id.sttStart);

        fab_plus = (FloatingActionButton) findViewById(R.id.plus);
        fab_minus = (FloatingActionButton) findViewById(R.id.minus);

        fab_sub1 = (FloatingActionButton) findViewById(R.id.fabsub1);
        fab_sub2 = (FloatingActionButton) findViewById(R.id.fabsub2);
        fab_sub3 = (FloatingActionButton) findViewById(R.id.fabsub3);

        voice_set = (Button) findViewById(R.id.voice_set);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        tv1_tts = (TextView) findViewById(R.id.tv1_tts);
        tv1_db = (TextView) findViewById(R.id.tv1_db);
        tv1_pron = (TextView) findViewById(R.id.tv1_pron);

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");


        sttBtn.setOnClickListener(v -> {
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mRecognizer.setRecognitionListener(listener);
            mRecognizer.startListening(intent);
        });

        fab_plus.setOnClickListener(v -> {
            textView.setTextSize(Dimension.DP, textView.getTextSize() + 5.0f);
            Log.e("plus", String.valueOf(textView.getTextSize()));
        });

        fab_minus.setOnClickListener(v -> {

            textView.setTextSize(Dimension.DP, textView.getTextSize() - 5.0f);
            Log.e("minus", String.valueOf(textView.getTextSize()));
        });

        change1 = findViewById(R.id.change1);

        change1.setOnClickListener(v -> {
//            Intent intent= new Intent(SttActivity.this, TtsActivity.class);
//            startActivity(intent);

            if (isopen) {


                fab_sub1.startAnimation(fab_close);
                fab_sub1.setClickable(false);
                fab_sub2.startAnimation(fab_close);
                fab_sub2.setClickable(false);
                fab_sub3.startAnimation(fab_close);
                fab_sub3.setClickable(false);

                tv1_tts.startAnimation(fab_close);
                tv1_db.startAnimation(fab_close);
                tv1_pron.startAnimation(fab_close);
                isopen = false;
            } else {


                fab_sub1.startAnimation(fab_open);
                fab_sub1.setClickable(true);
                fab_sub2.startAnimation(fab_open);
                fab_sub2.setClickable(true);
                fab_sub3.startAnimation(fab_open);
                fab_sub3.setClickable(true);

                tv1_tts.startAnimation(fab_open);
                tv1_db.startAnimation(fab_open);
                tv1_pron.startAnimation(fab_open);
                isopen = true;
            }

        });

        fab_sub1.setOnClickListener(v -> {
            Intent intent = new Intent(SttActivity.this, PronounceCategoryActivity.class);
            startActivity(intent);


        });

        fab_sub2.setOnClickListener(v -> {
            Intent intent = new Intent(SttActivity.this, DBActivity.class);
            startActivity(intent);


        });

        fab_sub3.setOnClickListener(v -> {
            Intent intent = new Intent(SttActivity.this, TtsActivity.class);
            startActivity(intent);


        });
        voice_set.setOnClickListener(v -> {
            final PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
            getMenuInflater().inflate(R.menu.setting, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.set_message) {
                        AlertDialog.Builder dlg = new AlertDialog.Builder(SttActivity.this, R.style.Dialog);
                        dlg.setTitle("기본메세지 변경");
                        EditText et = new EditText(SttActivity.this);
                        et.setText(guide);
                        dlg.setView(et);


                        dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        dlg.setPositiveButton("설정", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("guide", et.getText().toString());
                                editor.apply();
                                Toast.makeText(getApplicationContext(), "변경 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }
                        });

                        dlg.show();

                    } else if (item.getItemId() == R.id.tutorial) {
                        Intent intent = new Intent(SttActivity.this, TutorialActivity.class);
                        startActivity(intent);
                        finish();
                    }


                    return false;
                }
            });
            popupMenu.show();
        });

    }


    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
        }

        @Override
        public void onEndOfSpeech() {
        }

        @Override
        public void onError(int error) {
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onResults(Bundle results) {
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < matches.size(); i++) {
                textView.setText(matches.get(i));
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }
    };


}
