package com.backend.model;

import java.util.Date;

public class Article {
    private int id;
    private String naslov;
    private String tekst;
    private Date vremeKreiranja;
    private int brojPoseta;
    private int autorId;
    private int destinacijaId;
    private String aktivnosti;
    public Article() {}

    public int getId() {
        return id;
    }

    public String getAktivnosti() {
        return aktivnosti;
    }
    public void setAktivnosti(String aktivnosti) {
        this.aktivnosti = aktivnosti;;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getNaslov() {
        return naslov;
    }

    public void setNaslov(String naslov) {
        this.naslov = naslov;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public Date getVremeKreiranja() {
        return vremeKreiranja;
    }

    public void setVremeKreiranja(Date vremeKreiranja) {
        this.vremeKreiranja = vremeKreiranja;
    }

    public int getBrojPoseta() {
        return brojPoseta;
    }

    public void setBrojPoseta(int brojPoseta) {
        this.brojPoseta = brojPoseta;
    }

    public int getAutorId() {
        return autorId;
    }

    public void setAutorId(int autorId) {
        this.autorId = autorId;
    }

    public int getDestinacijaId() {
        return destinacijaId;
    }

    public void setDestinacijaId(int destinacijaId) {
        this.destinacijaId = destinacijaId;
    }
}
