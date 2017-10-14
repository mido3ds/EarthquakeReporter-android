package com.example.android.quakereport;

public class Earthquake {
    private Double magnitude;
    private String place;
    private String date;

    public Earthquake(Double magnitude, String place, String date) {
        this.magnitude = magnitude;
        this.place = place;
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public String getDate() {
        return date;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    @Override
    public String toString() {
        return "Earthquake{" +
                "place='" + place + '\'' +
                ", date='" + date + '\'' +
                ", magnitude=" + magnitude +
                '}';
    }
}
