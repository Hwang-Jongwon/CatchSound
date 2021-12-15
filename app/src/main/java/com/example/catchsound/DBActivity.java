package com.example.catchsound;

import android.app.Dialog;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DBActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;

    private RecyclerView RecyclerView_main;

    Dialog custom_dialog;

    private com.example.catchsound.DBHelper dbHelper;

    private FloatingActionButton floatingActionButton;

    private com.example.catchsound.nAdapter itemAdapter;

    private ArrayList<com.example.catchsound.TodoItem> todoItems;


    private FloatingActionButton change3;
    private FloatingActionButton db_fab1;
    private FloatingActionButton db_fab2;
    private FloatingActionButton db_fab3;
    private TextView tv3_stt;
    private TextView tv3_tts;
    private TextView tv3_pron;
    private boolean isopen = false;
    private Animation fab_open, fab_close;
    private long time = 0;
    private int flag = 1;

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

    private void LoadRecentDB() {
        todoItems = dbHelper.getNameList();
        if (itemAdapter == null) {
            itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
            RecyclerView_main.setHasFixedSize(true);
            RecyclerView_main.setAdapter(itemAdapter);
        }
    }

    private void setDB() {
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                custom_dialog.setContentView(R.layout.custom_dialog);
                EditText editcontent = custom_dialog.findViewById(R.id.editContent);
                EditText edittag = custom_dialog.findViewById(R.id.editTag);
                Button button_save = custom_dialog.findViewById(R.id.button_save);
                Button button_cancel = custom_dialog.findViewById(R.id.button_cancel);
                edittag.setText("기타");
                button_save.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                        dbHelper.InsertTodo(editcontent.getText().toString(), curTime, "1", R.drawable.star_full, edittag.getText().toString(), "0");
                        com.example.catchsound.TodoItem todoItem = new com.example.catchsound.TodoItem();
                        todoItem.setContent(editcontent.getText().toString());
                        todoItem.setFlag("1");
                        todoItem.setStar(R.drawable.star_full);
                        todoItem.setDate(curTime);
                        todoItem.setTag(edittag.getText().toString());
                        todoItem.setUse("0");
                        itemAdapter.addItem(todoItem);
                        itemAdapter.notifyDataSetChanged();
                        RecyclerView_main.smoothScrollToPosition(0);
                        custom_dialog.dismiss();
                    }
                });
                button_cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View param2View) {
                        custom_dialog.dismiss();
                    }
                });
                custom_dialog.show();
            }
        });
        itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
        RecyclerView_main.setHasFixedSize(true);
        RecyclerView_main.setAdapter(itemAdapter);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setElevation(0);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        dbHelper = new com.example.catchsound.DBHelper(this);
        todoItems = new ArrayList<com.example.catchsound.TodoItem>();
        RecyclerView_main = findViewById(R.id.RecyclerView_main);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        custom_dialog = new Dialog(DBActivity.this);
        custom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LoadRecentDB();
        setDB();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(this, ListViewWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);


        change3 = (FloatingActionButton) findViewById(R.id.change3);
        db_fab1 = (FloatingActionButton) findViewById(R.id.db_fab1);
        db_fab2 = (FloatingActionButton) findViewById(R.id.db_fab2);
        db_fab3 = (FloatingActionButton) findViewById(R.id.db_fab3);
        tv3_tts = (TextView) findViewById(R.id.tv3_tts);
        tv3_stt = (TextView) findViewById(R.id.tv3_stt);
        tv3_pron = (TextView) findViewById(R.id.tv3_pron);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);


        change3.setOnClickListener(v -> {

            if (isopen) {
                db_fab1.startAnimation(fab_close);
                db_fab1.setClickable(false);
                db_fab2.startAnimation(fab_close);
                db_fab2.setClickable(false);
                db_fab3.startAnimation(fab_close);
                db_fab3.setClickable(false);

                tv3_stt.startAnimation(fab_close);
                tv3_tts.startAnimation(fab_close);
                tv3_pron.startAnimation(fab_close);

                isopen = false;
            } else {

                db_fab1.startAnimation(fab_open);
                db_fab1.setClickable(true);
                db_fab2.startAnimation(fab_open);
                db_fab2.setClickable(true);
                db_fab3.startAnimation(fab_open);
                db_fab3.setClickable(true);

                tv3_stt.startAnimation(fab_open);
                tv3_tts.startAnimation(fab_open);
                tv3_pron.startAnimation(fab_open);

                isopen = true;
            }


        });

        db_fab1.setOnClickListener(v -> {

            Intent intent = new Intent(DBActivity.this, SttActivity.class);
            startActivity(intent);

        });

        db_fab2.setOnClickListener(v -> {

            Intent intent = new Intent(DBActivity.this, TtsActivity.class);
            startActivity(intent);

        });

        db_fab3.setOnClickListener(v -> {

            Intent intent = new Intent(DBActivity.this, PronounceCategoryActivity.class);
            startActivity(intent);

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(flag == 1)
                    searchContact(query);
                else
                    searchContactTag(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(flag == 1)
                    searchContact(newText);
                else
                    searchContactTag(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                todoItems = dbHelper.getTagList();
                itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
                RecyclerView_main.setHasFixedSize(true);
                RecyclerView_main.setAdapter(itemAdapter);
                break;

            case R.id.menu2:
                todoItems = dbHelper.getNameList();
                itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
                RecyclerView_main.setHasFixedSize(true);
                RecyclerView_main.setAdapter(itemAdapter);
                break;

            case R.id.menu3:
                todoItems = dbHelper.getStarList();
                itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
                RecyclerView_main.setHasFixedSize(true);
                RecyclerView_main.setAdapter(itemAdapter);
                break;

            case R.id.menu4:
                todoItems = dbHelper.getUseList();
                itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
                RecyclerView_main.setHasFixedSize(true);
                RecyclerView_main.setAdapter(itemAdapter);
                break;

            case R.id.menu5:
                flag = 1;
                break;

            case R.id.menu6:
                flag = 0;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchContact(String keyword) {
        todoItems = dbHelper.SearchList(keyword);
        itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
        RecyclerView_main.setHasFixedSize(true);
        RecyclerView_main.setAdapter(itemAdapter);
    }

    private void searchContactTag(String keyword) {
        todoItems = dbHelper.SearchTagList(keyword);
        itemAdapter = new com.example.catchsound.nAdapter(todoItems, this);
        RecyclerView_main.setHasFixedSize(true);
        RecyclerView_main.setAdapter(itemAdapter);
    }


}
