package com.example.catchsound;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar ab = getSupportActionBar() ;
        ab.hide();
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), DBActivity.class);
                    startActivity(intent);
            }
        });

        //발음 교정 카테고리 액티비티로 이동
        Button btn2 = findViewById(R.id.btn_pronounce);
        btn2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceCategoryActivity.class);
                startActivity(intent);
            }
        });

        Button btn3 = findViewById(R.id.btn_stt);
        btn3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SttActivity.class);
                startActivity(intent);
            }
        });


        Button btn4 = findViewById(R.id.btn_tts);
        btn4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TtsActivity.class);
                startActivity(intent);
            }
        });
    }
}