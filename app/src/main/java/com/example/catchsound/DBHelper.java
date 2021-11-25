package com.example.catchsound;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ImageView;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "catchsound.db";

    private static final int DB_VERSION = 9;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void DeleteTodo(String _beforeDate) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM TodoList WHERE date = '" + _beforeDate + "'");
    }

    public void InsertTodo(String _content, String _date, String _flag) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO TodoList (content, date, flag) VALUES('" + _content + "','" + _date + "','" +_flag + "');");
    }

    public void UpdateTodo(String _content, String _date, String _beforeDate, String _flag) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE TodoList SET content='" + _content + "', date='" + _date + "', flag='" +_flag + "' WHERE date='" + _beforeDate + "'");
    }

    public ArrayList<TodoItem> getTodoList() {
        ArrayList<TodoItem> arrayList = new ArrayList();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM TodoList ORDER BY date asc", null);
        if (cursor.getCount() != 0)
            while (cursor.moveToNext()) {
                int i = cursor.getInt(cursor.getColumnIndex("id"));
                String str2 = cursor.getString(cursor.getColumnIndex("content"));
                String str3 = cursor.getString(cursor.getColumnIndex("date"));
                String flag = cursor.getString(cursor.getColumnIndex(("flag")));
                TodoItem todoItem = new TodoItem();
                todoItem.setId(i);
                todoItem.setContent(str2);
                todoItem.setDate(str3);
                todoItem.setFlag(flag);
                arrayList.add(todoItem);
            }
        cursor.close();
        return arrayList;
    }

    public ArrayList<TodoItem> getNameList() {
        ArrayList<TodoItem> arrayList = new ArrayList();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM TodoList ORDER BY content", null);
        if (cursor.getCount() != 0)
            while (cursor.moveToNext()) {
                int i = cursor.getInt(cursor.getColumnIndex("id"));
                String str2 = cursor.getString(cursor.getColumnIndex("content"));
                String str3 = cursor.getString(cursor.getColumnIndex("date"));
                String flag = cursor.getString(cursor.getColumnIndex(("flag")));
                TodoItem todoItem = new TodoItem();
                todoItem.setId(i);
                todoItem.setContent(str2);
                todoItem.setDate(str3);
                todoItem.setFlag(flag);
                arrayList.add(todoItem);
            }
        cursor.close();
        return arrayList;
    }

    public ArrayList<TodoItem> getStarList() {
        ArrayList<TodoItem> arrayList = new ArrayList();
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM TodoList WHERE flag = 1 ORDER BY content", null);
        if (cursor.getCount() != 0)
            while (cursor.moveToNext()) {
                int i = cursor.getInt(cursor.getColumnIndex("id"));
                String str2 = cursor.getString(cursor.getColumnIndex("content"));
                String str3 = cursor.getString(cursor.getColumnIndex("date"));
                String flag = cursor.getString(cursor.getColumnIndex(("flag")));
                TodoItem todoItem = new TodoItem();
                todoItem.setId(i);
                todoItem.setContent(str2);
                todoItem.setDate(str3);
                todoItem.setFlag(flag);
                arrayList.add(todoItem);
            }
        cursor.close();
        return arrayList;
    }

    public ArrayList<TodoItem> SearchList(String keyword) {
        ArrayList<TodoItem> arrayList = new ArrayList();
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM TodoList WHERE content LIKE ?", new String[]{"%" + keyword + "%"});
        if (cursor.getCount() != 0)
            while (cursor.moveToNext()) {
                int i = cursor.getInt(cursor.getColumnIndex("id"));
                String str2 = cursor.getString(cursor.getColumnIndex("content"));
                String str3 = cursor.getString(cursor.getColumnIndex("date"));
                String flag = cursor.getString(cursor.getColumnIndex(("flag")));
                TodoItem todoItem = new TodoItem();
                todoItem.setId(i);
                todoItem.setContent(str2);
                todoItem.setDate(str3);
                todoItem.setFlag(flag);
                arrayList.add(todoItem);
            }
        return arrayList;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS TodoList(id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, date TEXT NOT NULL, flag String NOT NULL)");
    }

    public void onDestroy(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL("DROP TABLE TodoList");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS TodoList");
        onCreate(db);
    }
}
