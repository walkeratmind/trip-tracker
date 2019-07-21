package com.rakesh.triptracker;

import com.google.android.gms.maps.model.LatLng;

public class Constants {

    public static final int ERROR_DIALOG_REQUEST = 9001;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    public static final float DEFAULT_ZOOM = 8f;

    public static final LatLng nepal = new LatLng(27.7172, 85.3240);

    public static final int REQUEST_CHECK_SETTINGS = 100;

    public static final int REQUEST_PLACE_PICKER = 1;


    // for petrol station KEYs by arbind
    public static final String BROWSER_KEY = "AIzaSyBsJyAKr9zTWqlAiLwdmI-u7vIqnu-_hg0";
    public static final String SERVER_API_KEY = "AIzaSyDYTQAQeHD6xuxxuKV36DLAczDw2IzRKWk";

    // for firestore db Collections
    public static final String USER_COLLECTION = "users";
    public static final String VISITED_COLLECTION = "visited";


}
