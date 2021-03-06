package com.example.catchsound;

import android.Manifest;
import android.content.Intent;
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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;


public class PronounceActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "prefs";
    private static final String MSG_KEY = "status";
    private static final String accessKey = "6e40005e-26a1-44df-8032-8d73501924fb";
    private static int MICROPHONE_PERMISSION_CODE = 100;

    TextView textMike;
    TextView sentence;
    TextView review;
    ImageButton buttonBefore;
    ImageButton buttonNext;
    ArrayList<String> sentenceList;
    private int nowsentenceIdx = 0;
    private int finalsentenceIdx;

    String result;
    RatingBar ratingbar;

    int maxLenSpeech = 16000 * 45;
    byte[] speechData = new byte[maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public synchronized void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            String v = bd.getString(MSG_KEY);
            switch (msg.what) {
                //?????? ??????
                case 1:
                    textMike.setText(v);
                    break;
                //????????? ??????????????? ?????????(?????? ????????????, ?????? ???)
                case 2:
                    textMike.setText(v);
                    sentence.setEnabled(false);
                    break;
                //????????? ?????????????????? ?????????(????????? ?????? ?????? ??????)
                case 3:
                    textMike.setText(v);
                    break;
                //????????? ?????????????????? ?????????
                case 4:
                    textMike.setText(v);
                    sentence.setEnabled(true);
                    break;
                //????????? ??????????????? ?????????
                case 5:
                    //????????? ??????, ?????? ????????????
                    int recognized_idx = result.indexOf("recognized");
                    int score_idx = result.indexOf("score");
                    if (score_idx - recognized_idx == 16) {
                        textMike.setText("????????? ?????? ????????????. ?????? ????????? ?????????!");
                    } else {
                        String recognized = result.substring(recognized_idx + 13, score_idx - 3);
                        String score = result.substring(score_idx + 7, score_idx + 12);
                        Double double_score = (Double) Double.parseDouble(score);
                        double_score = (Double) (Math.round(double_score * 10) / 10.0);
                        Float float_score = double_score.floatValue();

                        ratingbar.setRating(float_score);
                        textMike.setText("");
                        review.setText("\n\"" + recognized + "\" ?????? ?????????!");
                    }

                    sentence.setEnabled(true);
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
        ActionBar ab = getSupportActionBar();
        ab.hide();

        //????????? ????????????
        Intent intent = getIntent();
        String categoryid = intent.getStringExtra("categoryname");
        sentenceList = new ArrayList<>();

        if (categoryid.equals("greeting")) {
            getSentences(1);
        } else if (categoryid.equals("weather")) {
            getSentences(2);
        } else if (categoryid.equals("emotion")) {
            getSentences(3);
        } else if (categoryid.equals("taste")) {
            getSentences(4);
        } else if (categoryid.equals("ordering")) {
            getSentences(5);
        } else if (categoryid.equals("shopping")) {
            getSentences(6);
        } else if (categoryid.equals("transportation")) {
            getSentences(7);
        }

        //????????? ?????? ??????
        if (isMicrophonePresent()) {
            getMicrophonePermission();
        }

        textMike = (TextView) this.findViewById(R.id.textResult);
        sentence = (TextView) this.findViewById(R.id.sentence);
        review = (TextView) this.findViewById(R.id.review);
        buttonBefore = (ImageButton) this.findViewById(R.id.btn_before);
        buttonNext = (ImageButton) this.findViewById(R.id.btn_next);
        sentence.setText(sentenceList.get(nowsentenceIdx));
        ratingbar = (RatingBar) this.findViewById(R.id.ratingbar);

        //??????????????? ??????
        Animation anim_fadein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha);
        Animation anim_fadeout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeout);
        textMike.startAnimation(anim_fadein);
        sentence.startAnimation(anim_fadein);
        review.startAnimation(anim_fadein);

        Log.i("????????? ??? ?????????", "" + finalsentenceIdx);

        //??????, ?????? ?????? ??????
        buttonBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review.setText("");
                ratingbar.setRating(0);
                if (nowsentenceIdx > 0) {
                    nowsentenceIdx -= 1;
                    sentence.setText(sentenceList.get(nowsentenceIdx));
                } else {
                    Toast.makeText(getApplicationContext(), "??? ?????? ???????????????!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                review.setText("");
                ratingbar.setRating(0);
                if (nowsentenceIdx < finalsentenceIdx - 2) {
                    nowsentenceIdx++;
                    sentence.setText(sentenceList.get(nowsentenceIdx));
                } else {
                    Toast.makeText(getApplicationContext(), "??? ????????? ???????????????!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        sentence.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isRecording) {
                    forceStop = true;
                } else {
                    try {
                        new Thread(new Runnable() {
                            public void run() {
                                SendMessage("?????? ???..\n\n????????? ?????? ??? ????????? ????????? ????????????!", 1);
                                try {
                                    recordSpeech();
                                    SendMessage("?????? ?????????..", 2);
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
                        textMike.setText("ERROR: " + t.toString());
                        forceStop = false;
                        isRecording = false;
                    }
                }
            }
        });
    }

    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return true;
        } else {
            return false;
        }
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, MICROPHONE_PERMISSION_CODE);
        }
    }

    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
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
            } else {
                short[] inBuffer = new short[bufferSize];
                forceStop = false;
                isRecording = true;
                audio.startRecording();
                while (!forceStop) {
                    int ret = audio.read(inBuffer, 0, bufferSize);
                    for (int i = 0; i < ret; i++) {
                        if (lenSpeech >= maxLenSpeech) {
                            forceStop = true;
                            break;
                        }
                        speechData[lenSpeech * 2] = (byte) (inBuffer[i] & 0x00FF);
                        speechData[lenSpeech * 2 + 1] = (byte) ((inBuffer[i] & 0xFF00) >> 8);
                        lenSpeech++;
                    }
                }
                audio.stop();
                audio.release();
                isRecording = false;
            }
        } catch (Throwable t) {
            throw new RuntimeException(t.toString());
        }
    }

    public String sendDataAndGetResult() {
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/RecognitionKor";
        String languageCode;
        String audioContents;

        Gson gson = new Gson();
        languageCode = "korean";
        openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/PronunciationKor";

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech * 2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        argument.put("audio", audioContents);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        Integer responseCode;
        String responBody;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            if (responseCode == 200) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                responBody = readStream(is);
                return responBody;
            } else
                return "ERROR: " + Integer.toString(responseCode);
        } catch (Throwable t) {
            return "ERROR: " + t.toString();
        }
    }

    protected void getSentences(int colnum) {
        try {
            InputStream is = getResources().getAssets().open("prondb.xls");
            Workbook wb = Workbook.getWorkbook(is);
            Sheet sheet = wb.getSheet(0);
            if (sheet != null) {
                int rowTotal = sheet.getColumn(colnum).length; //?????? ???
                finalsentenceIdx = rowTotal;

                StringBuilder sb;
                for (int row = 1; row < rowTotal; row++) {
                    sb = new StringBuilder();

                    String sentence = sheet.getCell(colnum, row).getContents(); //colnum??? ?????? ?????? ???????????? ?????????
                    sb.append(sentence);

                    sentenceList.add(sb.toString());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

}
