package com.example.lab09;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GeoData {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public long createdAt;

    @ColumnInfo(name = "longitude")
    public double lng;

    @ColumnInfo(name = "latitude")
    public double lat;

    @ColumnInfo(name = "address")
    public String address;

    public double  getLng() {
        return lng;
    }

    public double getLat() {
        return lat;
    }

    public String getAddress() {
        return address;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
