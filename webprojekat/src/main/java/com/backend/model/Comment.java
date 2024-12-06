package com.backend.model;

import java.util.Date;

public class Comment {
    private int id;
    private String autor_ime;
    private String tekst;
    private Date datumKreiranja;
    private int clanakId;

    // Konstruktori
    public Comment() {}

    // Getteri i setteri
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAutorIme() {
        return this.autor_ime;
    }

    public void setAutorIme(String autorIme) {
        this.autor_ime = autorIme;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public Date getDatumKreiranja() {
        return datumKreiranja;
    }

    public void setDatumKreiranja(Date datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }

    public int getClanakId() {
        return clanakId;
    }

    public void setClanakId(int clanakId) {
        this.clanakId = clanakId;
    }
}
