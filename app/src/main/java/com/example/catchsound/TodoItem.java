package com.example.catchsound;

public class TodoItem {
    private String content;

    private String date;

    private int id;

    private String flag;

    private int star;

    private String tag;

    private String use;

    public String getUse() {
        return this.use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

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

    public int getStar() {
        return this.star;
    }

    public void setStar(int star) {
        this.star = star;
    }
}
