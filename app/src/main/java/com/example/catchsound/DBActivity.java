package com.example.catchsound;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        getSupportActionBar().setTitle("");
        this.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialog dialog = new Dialog(DBActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.activity_edit);
                EditText edittitle = dialog.findViewById(R.id.editTitle);
                EditText editcontent = dialog.findViewById(R.id.editContent);
                Button button_save = dialog.findViewById(R.id.button_save);
                Button button_cancel = dialog.findViewById(R.id.button_cancel);
                button_save.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        String curTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
                        dbHelper.InsertTodo(edittitle.getText().toString(), editcontent.getText().toString(), curTime);
                        com.example.catchsound.TodoItem todoItem = new com.example.catchsound.TodoItem();
                        todoItem.setTitle(edittitle.getText().toString());
                        todoItem.setContent(editcontent.getText().toString());
                        todoItem.setDate(curTime);
                        itemAdapter.addItem(todoItem);
                        itemAdapter.notifyDataSetChanged();
                        RecyclerView_main.smoothScrollToPosition(0);
                        dialog.dismiss();
                    }
                });
                button_cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View param2View) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
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
        getSupportActionBar().setElevation(0);
        dbHelper = new com.example.catchsound.DBHelper(this);
        todoItems = new ArrayList<com.example.catchsound.TodoItem>();
        RecyclerView_main = findViewById(R.id.RecyclerView_main);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        LoadRecentDB();
        setDB();
    }
}
