package com.example.catchsound;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "catchsound.db";

    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void DeleteTodo(String _beforeDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM TodoList WHERE date = '" + _beforeDate +"'");
    }

    public void InsertTodo(String _title, String _content, String _date) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO TodoList (title, content, date) VALUES('" + _title + "', '" + _content + "','" + _date + "');");
    }

    public void UpdateTodo(String _title, String _content, String _date, String _beforeDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TodoList SET title='" + _title + "', content='" + _content + "', date='" + _date + "' WHERE date='" + _beforeDate + "'");
    }

    public ArrayList<TodoItem> getTodoList() {
        ArrayList<TodoItem> arrayList = new ArrayList();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM TodoList ORDER BY date", null);
        if (cursor.getCount() != 0)
            while (cursor.moveToNext()) {
                int i = cursor.getInt(cursor.getColumnIndex("id"));
                String str1 = cursor.getString(cursor.getColumnIndex("title"));
                String str2 = cursor.getString(cursor.getColumnIndex("content"));
                String str3 = cursor.getString(cursor.getColumnIndex("date"));
                TodoItem todoItem = new TodoItem();
                todoItem.setId(i);
                todoItem.setTitle(str1);
                todoItem.setContent(str2);
                todoItem.setDate(str3);
                arrayList.add(todoItem);
            }
        cursor.close();
        return arrayList;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS TodoList(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, date TEXT NOT NULL)");
    }

    public void onDestroy(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL("DROP TABLE TodoList");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TodoList");
        onCreate(db);
    }
}
