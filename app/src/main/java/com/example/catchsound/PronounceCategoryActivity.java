package com.example.catchsound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;



public class PronounceCategoryActivity extends AppCompatActivity {

    CardView card_greeting;
    CardView card_weather;
    CardView card_emotion;
    CardView card_flavor;
    CardView card_orderfood;
    CardView card_shopping;
    CardView card_publictransport;

    private FloatingActionButton change4;
    private FloatingActionButton pron_fab1;
    private FloatingActionButton pron_fab2;
    private FloatingActionButton pron_fab3;
    private TextView tv4_stt;
    private TextView tv4_tts;
    private TextView tv4_db;
    private boolean isopen=false;
    private Animation fab_open, fab_close;

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
        setContentView(R.layout.activity_pron_category);
        ActionBar ab = getSupportActionBar() ;
        ab.hide();

        //인사 카테고리 클릭
        card_greeting = findViewById(R.id.card_greeting);
        card_greeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceActivity.class);
                intent.putExtra("categoryname", "greeting");
                startActivity(intent);
            }
        });
        //날씨 카테고리 클릭
        card_weather = findViewById(R.id.card_weather);
        card_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceActivity.class);
                intent.putExtra("categoryname", "weather");
                startActivity(intent);
            }
        });
        //감정 카테고리 클릭
        card_emotion = findViewById(R.id.card_emotion);
        card_emotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceActivity.class);
                intent.putExtra("categoryname", "emotion");
                startActivity(intent);
            }
        });
        //맛표현 카테고리 클릭
        card_flavor = findViewById(R.id.card_flavor);
        card_flavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceActivity.class);
                intent.putExtra("categoryname", "taste");
                startActivity(intent);
            }
        });
        //음식주문 카테고리 클릭
        card_orderfood = findViewById(R.id.card_orderfood);
        card_orderfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceActivity.class);
                intent.putExtra("categoryname", "ordering");
                startActivity(intent);
            }
        });
        //쇼핑 카테고리 클릭
        card_shopping = findViewById(R.id.card_shopping);
        card_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceActivity.class);
                intent.putExtra("categoryname", "shopping");
                startActivity(intent);
            }
        });
        //대중교통 카테고리 클릭
        card_publictransport = findViewById(R.id.card_publictransport);
        card_publictransport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PronounceActivity.class);
                intent.putExtra("categoryname", "transportation");
                startActivity(intent);
            }
        });
///////////////////////////////////////////////////////////
        change4=(FloatingActionButton)findViewById(R.id.change4);
        pron_fab1=(FloatingActionButton)findViewById(R.id.pron_fab1);
        pron_fab2=(FloatingActionButton)findViewById(R.id.pron_fab2);
        pron_fab3=(FloatingActionButton)findViewById(R.id.pron_fab3);
        tv4_tts=(TextView)findViewById(R.id.tv4_tts);
        tv4_stt=(TextView)findViewById(R.id.tv4_stt);
        tv4_db=(TextView)findViewById(R.id.tv4_db);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);


        change4.setOnClickListener(v->{

            if(isopen) {
                pron_fab1.startAnimation(fab_close);
                pron_fab1.setClickable(false);
                pron_fab2.startAnimation(fab_close);
                pron_fab2.setClickable(false);
                pron_fab3.startAnimation(fab_close);
                pron_fab3.setClickable(false);

                tv4_stt.startAnimation(fab_close);
                tv4_tts.startAnimation(fab_close);
                tv4_db.startAnimation(fab_close);

                isopen=false;
            }


            else{

                pron_fab1.startAnimation(fab_open);
                pron_fab1.setClickable(true);
                pron_fab2.startAnimation(fab_open);
                pron_fab2.setClickable(true);
                pron_fab3.startAnimation(fab_open);
                pron_fab3.setClickable(true);

                tv4_stt.startAnimation(fab_open);
                tv4_tts.startAnimation(fab_open);
                tv4_db.startAnimation(fab_open);

                isopen=true;
            }


        });


        pron_fab1.setOnClickListener(v -> {
            Intent intent= new Intent(PronounceCategoryActivity.this, SttActivity.class);
            startActivity(intent);
        });

        pron_fab2.setOnClickListener(v -> {
            Intent intent= new Intent(PronounceCategoryActivity.this, TtsActivity.class);
            startActivity(intent);
        });

        pron_fab3.setOnClickListener(v -> {
            Intent intent= new Intent(PronounceCategoryActivity.this, DBActivity.class);
            startActivity(intent);
        });



    }
}
