package com.rakesh.triptracker.fragment;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rakesh.triptracker.Constants;
import com.rakesh.triptracker.R;
import com.rakesh.triptracker.RegisterActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;

    private LatLng pickedLocation;

    Button setLocationBtn;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync( this);
        }

        progressDialog = new ProgressDialog(getContext());

        setLocationBtn = view.findViewById(R.id.set_location_btn);

        mAuth = FirebaseAuth.getInstance();
        db =FirebaseFirestore.getInstance();

        setLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUid = mAuth.getCurrentUser().getUid();

                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", currentUid);
                userData.put("latlng", pickedLocation);

                progressDialog.setMessage("Adding your moment...");
                progressDialog.show();
                db.collection(Constants.VISITED_COLLECTION)
                        .add(userData)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Successfully Added...", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(getContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constants.nepal, Constants.DEFAULT_ZOOM ));
        // Add a marker in Sydney and move the camera
//        LatLng Nepal = new LatLng(27, 85);
//        mMap.addMarker(new MarkerOptions().position(Nepal).title("Marker in nepal"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(Nepal));

        locationPicker();
    }

    public void locationPicker() {
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                pickedLocation = cameraPosition.target;
                Log.d(TAG, "Picked Location: " + pickedLocation.toString());
            }
        });
    }
}
