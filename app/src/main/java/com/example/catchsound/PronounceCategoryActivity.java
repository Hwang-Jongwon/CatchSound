package com.example.catchsound;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

public class PronounceCategoryActivity extends AppCompatActivity {

    CardView card_greeting;
    CardView card_weather;
    CardView card_emotion;
    CardView card_flavor;
    CardView card_orderfood;
    CardView card_shopping;
    CardView card_publictransport;

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

    }
}
