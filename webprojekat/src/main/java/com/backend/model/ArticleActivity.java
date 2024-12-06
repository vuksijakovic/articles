package com.backend.model;

public class ArticleActivity {
    private int id;
    private int clanakId;
    private int aktivnostId;


    public ArticleActivity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClanakId() {
        return clanakId;
    }

    public void setClanakId(int clanakId) {
        this.clanakId = clanakId;
    }

    public int getAktivnostId() {
        return aktivnostId;
    }

    public void setAktivnostId(int aktivnostId) {
        this.aktivnostId = aktivnostId;
    }
}
