package com.example.catchsound;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public class TtsActivity extends Activity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private Button btn_Speak;
    private EditText txtText;
    private FloatingActionButton change2;
    FloatingActionButton fab_sub4;
    FloatingActionButton fab_sub5;
    FloatingActionButton fab_sub6;

    TextView tv2_stt;
    TextView tv2_db;
    TextView tv2_pron;

    private Animation fab_open, fab_close;

    boolean isopen = false;

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
        setContentView(R.layout.activity_tts);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        tts = new TextToSpeech(this, this);
        btn_Speak = findViewById(R.id.btnSpeak);
        txtText = findViewById(R.id.txtText);
        change2 = findViewById(R.id.change2);

        fab_sub4=(FloatingActionButton)findViewById(R.id.fabsub4);
        fab_sub5=(FloatingActionButton)findViewById(R.id.fabsub5);
        fab_sub6=(FloatingActionButton)findViewById(R.id.fabsub6);

        tv2_stt=(TextView)findViewById(R.id.tv2_stt);
        tv2_db=(TextView)findViewById(R.id.tv2_db);
        tv2_pron=(TextView)findViewById(R.id.tv2_pron);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        btn_Speak.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                speakOut();
            }

        });

        change2.setOnClickListener(v -> {
//            Intent intent= new Intent(TtsActivity.this, SttActivity.class);
//            startActivity(intent);
            if(isopen) {
            fab_sub4.startAnimation(fab_close);
            fab_sub4.setClickable(false);
            fab_sub5.startAnimation(fab_close);
            fab_sub5.setClickable(false);
            fab_sub6.startAnimation(fab_close);
            fab_sub6.setClickable(false);

                tv2_stt.startAnimation(fab_close);
                tv2_db.startAnimation(fab_close);
                tv2_pron.startAnimation(fab_close);
            isopen=false;
            }


            else{

            fab_sub4.startAnimation(fab_open);
            fab_sub4.setClickable(true);
            fab_sub5.startAnimation(fab_open);
            fab_sub5.setClickable(true);
            fab_sub6.startAnimation(fab_open);
            fab_sub6.setClickable(true);

                tv2_stt.startAnimation(fab_open);
                tv2_db.startAnimation(fab_open);
                tv2_pron.startAnimation(fab_open);
            isopen=true;
        }
        });

        fab_sub4.setOnClickListener(v->{
            Intent intent= new Intent(TtsActivity.this, SttActivity.class);
            startActivity(intent);



        });

        fab_sub5.setOnClickListener(v->{
            Intent intent= new Intent(TtsActivity.this, DBActivity.class);
            startActivity(intent);


        });

        fab_sub6.setOnClickListener(v->{
            Intent intent= new Intent(TtsActivity.this, PronounceCategoryActivity.class);
            startActivity(intent);


        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut() {
        CharSequence text = txtText.getText();

        AudioManager volumeControl = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if(volumeControl.getStreamVolume(AudioManager.STREAM_MUSIC) == 0)
            Toast.makeText(this, "소리가 꺼져있어요!", Toast.LENGTH_SHORT).show();

        tts.setPitch((float) 0.7);
        tts.setSpeechRate((float) 0.9);

        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    @Override
    public void onDestroy() {
        if (tts != null)  {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS)  {
            int result = tts.setLanguage(Locale.KOREA);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btn_Speak.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}

