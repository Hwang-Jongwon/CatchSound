package com.example.catchsound;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class PronounceActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "prefs";
    private static final String MSG_KEY = "status";
    private static final String accessKey = "6e40005e-26a1-44df-8032-8d73501924fb";
    private static int MICROPHONE_PERMISSION_CODE = 100;

    ImageButton buttonStart;
    TextView textResult;

    String result;

    int maxLenSpeech = 16000 * 45;
    byte [] speechData = new byte [maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public synchronized void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            String v = bd.getString(MSG_KEY);
            switch (msg.what) {
                //녹음 시작
                case 1:
                    textResult.setText(v);
                    break;
                //녹음이 정상적으로 종료됨(버튼 눌렀거나, 시간 끝)
                case 2:
                    textResult.setText(v);
                    buttonStart.setEnabled(false);
                    break;
                //녹음이 비정상적으로 종료됨(마이크 권한 필요 등등)
                case 3:
                    textResult.setText(v);
                    break;
                //인식이 비정상적으로 종료됨
                case 4:
                    textResult.setText(v);
                    buttonStart.setEnabled(true);
                    break;
                //인식이 정상적으로 종료됨
                case 5:
                    //인식된 문장, 점수 보여주기
                    int recognized_idx = result.indexOf("recognized");
                    int score_idx = result.indexOf("score");
                    if (score_idx - recognized_idx == 16){
                        textResult.setText("녹음이 되지 않았어요. 다시 시도해 주세요!");
                    }
                    else{
                        String recognized = result.substring(recognized_idx + 13, score_idx - 3);
                        String score = result.substring(score_idx + 7, score_idx + 12);
                        Double double_score = (Double) Double.parseDouble(score);
                        double_score = (Double) (Math.round(double_score*10)/10.0);
                        score = Double.toString(double_score);
                        textResult.setText("인식된 문장 : " + recognized + "\n" + "점수 : " + score);
                    }

                    buttonStart.setEnabled(true);
                    //textResult.setText(result);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void SendMessage(String str, int id) {
        Message msg = handler.obtainMessage();
        Bundle bd = new Bundle();
        bd.putString(MSG_KEY, str);
        msg.what = id;
        msg.setData(bd);
        handler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pron);

        //마이크 권한 요청
        if (isMicrophonePresent()){
            getMicrophonePermission();
        }

        buttonStart = (ImageButton)this.findViewById(R.id.buttonStart);
        textResult = (TextView)this.findViewById(R.id.textResult);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        textResult.startAnimation(animation);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);


        buttonStart.setOnClickListener(new  View.OnClickListener() {
            public void onClick(View v) {
                if (isRecording) {
                    forceStop = true;
                } else {
                    try {
                        new Thread(new Runnable() {
                            public void run() {
                                SendMessage("녹음 중..\n마이크를 한번 더 터치해 녹음을 멈추세요!", 1);
                                try {
                                    recordSpeech();
                                    SendMessage("발음 분석중..", 2);
                                } catch (RuntimeException e) {
                                    SendMessage(e.getMessage(), 3);
                                    return;
                                }

                                Thread threadRecog = new Thread(new Runnable() {
                                    public void run() {
                                        result = sendDataAndGetResult();
                                    }
                                });
                                threadRecog.start();
                                try {
                                    threadRecog.join(20000);
                                    if (threadRecog.isAlive()) {
                                        threadRecog.interrupt();
                                        SendMessage("No response from server for 20 secs", 4);
                                    } else {
                                        SendMessage("OK", 5);
                                    }
                                } catch (InterruptedException e) {
                                    SendMessage("Interrupted", 4);
                                }
                            }
                        }).start();
                    } catch (Throwable t) {
                        textResult.setText("ERROR: " + t.toString());
                        forceStop = false;
                        isRecording = false;
                    }
                }
            }
        });
    }

    private boolean isMicrophonePresent(){
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        else {
            return false;
        }
    }

    private void getMicrophonePermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public void recordSpeech() throws RuntimeException {
        try {
            int bufferSize = AudioRecord.getMinBufferSize(
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audio = new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);
            lenSpeech = 0;
            if (audio.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new RuntimeException("ERROR: Failed to initialize audio device. Allow app to access microphone");
            }
            else {
                short [] inBuffer = new short [bufferSize];
                forceStop = false;
                isRecording = true;
                audio.startRecording();
                while (!forceStop) {
                    int ret = audio.read(inBuffer, 0, bufferSize);
                    for (int i = 0; i < ret ; i++ ) {
                        if (lenSpeech >= maxLenSpeech) {
                            forceStop = true;
                            break;
                        }
                        speechData[lenSpeech*2] = (byte)(inBuffer[i] & 0x00FF);
                        speechData[lenSpeech*2+1] = (byte)((inBuffer[i] & 0xFF00) >> 8);
                        lenSpeech++;
                    }
                }
                audio.stop();
                audio.release();
                isRecording = false;
            }
        } catch(Throwable t) {
            throw new RuntimeException(t.toString());
        }
    }

    public String sendDataAndGetResult () {
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/RecognitionKor";
        String languageCode;
        String audioContents;

        Gson gson = new Gson();
        languageCode = "korean";
        openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/PronunciationKor";

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech*2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        argument.put("audio", audioContents);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        Integer responseCode;
        String responBody;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            if ( responseCode == 200 ) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                responBody = readStream(is);
                return responBody;
            }
            else
                return "ERROR: " + Integer.toString(responseCode);
        } catch (Throwable t) {
            return "ERROR: " + t.toString();
        }
    }

}
