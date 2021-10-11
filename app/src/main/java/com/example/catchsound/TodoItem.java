package com.example.catchsound;

public class TodoItem {
    private String content;

    private String date;

    private int id;

    private String till;

    private String title;

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
