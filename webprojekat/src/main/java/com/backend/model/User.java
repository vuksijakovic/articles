package com.backend.model;

public class User {
    private int id;
    private String email;
    private String ime;
    private String prezime;
    private String tipKorisnika;
    private String status;
    private String lozinka;
    private String token;

    public User() {}

    public int getId() {
        return id;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getIme() {
        return ime;
    }
    public void setIme(String ime) {
        this.ime = ime;
    }
    public String getPrezime() {
        return prezime;
    }
    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }
    public String getTipKorisnika() {
        return tipKorisnika;
    }
    public void setTipKorisnika(String tipKorisnika) {
        this.tipKorisnika = tipKorisnika;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getLozinka() {
        return lozinka;
    }
    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }
}
