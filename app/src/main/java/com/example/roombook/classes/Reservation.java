package com.example.roombook.classes;

import java.util.Date;
import java.util.Locale;

public class Reservation {


    private int id, roomId;
    private long fromTime, toTime;
    private String userId, purpose;


    @Override
    public String toString() {
        return "Purpose: " + purpose +

                String.format(Locale.getDefault(), "\nFrom: %tc", new Date(getFromTime() * 1000)) +

                String.format(Locale.getDefault(), "\nTo: %tc", new Date(getToTime() * 1000));
    }

    public Reservation(long fromTime, long toTime, String userId, String purpose, int roomId) {

        this.fromTime = fromTime;

        this.toTime = toTime;

        this.userId = userId;

        this.purpose = purpose;

        this.roomId = roomId;

    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(int fromTime) {
        this.fromTime = fromTime;
    }



    public long getToTime() {
        return toTime;
    }

    public void setToTime(int toTime) {
        this.toTime = toTime;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }



    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
