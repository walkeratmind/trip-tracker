package com.rakesh.triptracker.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rakesh.triptracker.Constants;
import com.rakesh.triptracker.R;
import com.rakesh.triptracker.data.model.VisitedPlaces;

import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MainActivity";

    private GoogleMap mMap;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    VisitedPlaces visitedPlaces;

    ProgressDialog progressDialog;

    public MapFragment() {}


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Toast.makeText(getContext(), "map", Toast.LENGTH_SHORT).show();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment  mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync( this);
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng nepal = new LatLng(27, 85);
        // Add a marker in  and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nepal, Constants.DEFAULT_ZOOM ));

        db.collection(Constants.VISITED_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                MarkerOptions markerOptions = new MarkerOptions();

                                LatLng latLng;
                                Map<String, Object> location = (Map<String, Object>) documentSnapshot.get("latlng");
                                double latitude = (double) location.get("latitude");
                                double longitude = (double) location.get("longitude");
                                Log.d(TAG, "from firebase: " + location.toString());

                                latLng = new LatLng(latitude, longitude);
                                String userId = (String) documentSnapshot.get("userId").toString();
//                                String docId = documentSnapshot.getId();
                                markerOptions.title("User Info");
                                markerOptions.position(latLng);
                                markerOptions.snippet(userId);
                                mMap.addMarker(markerOptions);
                            }
                        } else {
                            Log.d(TAG, "Error loading data: "+ task.getException().toString());

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error loading data: "+ e.toString());
                    }
                });
//        LatLng Nepal = new LatLng(27, 85);
//        mMap.addMarker(new MarkerOptions().position(Nepal).title("Marker in nepal"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(Nepal));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String uid = marker.getSnippet().toString();

                progressDialog.setMessage("Loading...");
                progressDialog.show();

                db.collection(Constants.USER_COLLECTION)
                        .whereEqualTo("device_token", uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                        String email = documentSnapshot.get("email").toString();
                                        String username = documentSnapshot.get("fullName").toString();
                                        Log.d(TAG, "doc: " + documentSnapshot.toString());
                                        showAlertDialog(username, email);
                                    }
                                } else {
                                    Log.d(TAG,"Error in Task : " + task.getException().toString());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Error loading data: "+ e.toString());

                            }
                        });

            }
        });
    }

    public void showAlertDialog(String username, String email) {
        Dialog InfoDialog = new Dialog(getContext());
        InfoDialog.setContentView(R.layout.user_detail);
        InfoDialog.setTitle("Details");
        InfoDialog.setCancelable(true);

        // set custom dialog components
        TextView usernameText, emailText;
        usernameText = InfoDialog.findViewById(R.id.username);
        emailText = InfoDialog.findViewById(R.id.email);

        usernameText.setText(username);
        emailText.setText(email);


        InfoDialog.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoDialog.dismiss();
            }
        });

        InfoDialog.show();
    }


    @Override
    public void onStart() {
        super.onStart();
    }

}
