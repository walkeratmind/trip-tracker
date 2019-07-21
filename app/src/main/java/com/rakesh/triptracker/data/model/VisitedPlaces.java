package com.rakesh.triptracker.data.model;

import com.google.android.gms.maps.model.LatLng;

public class VisitedPlaces {

    private String userId;
    private LatLng latLng;

    public VisitedPlaces() {
    }

    public VisitedPlaces(String userId, LatLng latLng) {
        this.userId = userId;
        this.latLng = latLng;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
