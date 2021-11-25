package com.example.catchsound;

import android.widget.ImageView;

public class TodoItem {
    private String content;

    private String date;

    private int id;

    private String flag;

    public String getFlag() {
        return this.flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getContent() {
        return this.content;
    }

    public String getDate() {
        return this.date;
    }

    public int getId() {
        return this.id;
    }

    public void setContent(String paramString) {
        this.content = paramString;
    }

    public void setDate(String paramString) {
        this.date = paramString;
    }

    public void setId(int paramInt) {
        this.id = paramInt;
    }
}
