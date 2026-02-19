package com.revive.medicinereminder.model;

public class Medicine {
    private String id;
    private String name;
    private String dosage;
    private int hour;
    private int minute;
    private int stock;
    private String userId;

    // Required empty constructor for Firestore
    public Medicine() {
    }

    // Full constructor with ID
    public Medicine(String id, String name, String dosage, int hour, int minute, int stock, String userId) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.hour = hour;
        this.minute = minute;
        this.stock = stock;
        this.userId = userId;
    }

    // Constructor without ID (for Firestore auto ID)
    public Medicine(String name, String dosage, int hour, int minute, int stock, String userId) {
        this.name = name;
        this.dosage = dosage;
        this.hour = hour;
        this.minute = minute;
        this.stock = stock;
        this.userId = userId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
