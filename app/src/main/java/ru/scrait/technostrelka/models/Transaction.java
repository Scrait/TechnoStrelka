package ru.scrait.technostrelka.models;

import java.util.Date;

public class Transaction {
    public String category;
    public String date;
    public float sum;
    public String type;

    public Transaction() {

    }

    public Transaction(String category, float sum, String type) {
        this.category = category;
        this.date = new Date().toString();
        this.sum = sum;
        this.type = type;
    }

    public Transaction(String category, float sum, String type, String date) {
        this.category = category;
        this.date = new Date().toString();
        this.sum = sum;
        this.type = type;
    }
}
