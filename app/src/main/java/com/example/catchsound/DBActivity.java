package com.example.catchsound;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DBActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_MENU = 101;

    private RecyclerView RecyclerView_main;

    private long bbtn = 0L;

    Dialog custom_dialog;

    Calendar cal = Calendar.getInstance();

    private com.example.catchsound.DBHelper dbHelper;

    EditText edittill;

    private FloatingActionButton floatingActionButton;

    private com.example.catchsound.nAdapter itemAdapter;

    private ArrayList<com.example.catchsound.TodoItem> todoItems;

    private void LoadRecentDB() {
        todoItems = dbHelper.getTodoList();
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
                Button button_save = custom_dialog.findViewById(R.id.button_save);
                Button button_cancel = custom_dialog.findViewById(R.id.button_cancel);
                button_save.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                        dbHelper.InsertTodo(editcontent.getText().toString(), curTime);
                        com.example.catchsound.TodoItem todoItem = new com.example.catchsound.TodoItem();
                        todoItem.setContent(editcontent.getText().toString());
                        todoItem.setDate(curTime);
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


    }

    private void updateLabel() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        this.edittill.setText(simpleDateFormat.format(this.cal.getTime()));
    }
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_db);
        dbHelper = new com.example.catchsound.DBHelper(this);
        todoItems = new ArrayList<com.example.catchsound.TodoItem>();
        RecyclerView_main = findViewById(R.id.RecyclerView_main);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        custom_dialog = new Dialog(DBActivity.this);
        custom_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LoadRecentDB();
        setDB();
    }
}
