package ru.scrait.technostrelka.models;

import java.util.Date;

public class User {
    public String email, password;
    public float balance;
    public float reservedSum;
    public String date;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.date = new Date().toString();
        this.balance = 0;
        this.reservedSum = 0;
    }
}
