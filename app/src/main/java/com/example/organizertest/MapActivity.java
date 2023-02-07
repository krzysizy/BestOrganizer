package com.example.organizertest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (Variables.isInclusive()) {
            String text = "Map is Ready";
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        }
        mMap = googleMap;

        if(Variables.isInclusive()) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night));
        }

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }
    }

    private static final String TAG = "MapActivity1";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private Address saveAddress;
    private String searchAddress;


    private AutoCompleteTextView mSearchText;
    private ImageView mGps;
    private ImageView search;
    private ImageView mSave;
    private ImageView mBack;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    AutocompleteSessionToken autocompleteSessionToken;
    PlacesClient placesClient;
    private TextToSpeech tts;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Variables.isInclusive()) {
            setContentView(R.layout.activity_map_inclusive);
        } else {
            setContentView(R.layout.activity_map);
        }



        getSupportActionBar().hide();

        searchAddress = getIntent().getStringExtra("address");

        Places.initialize(getApplicationContext(), "AIzaSyD4Oxr-o3z4qIj6FKNZ-SRIh2MaZMzc8JM");
        autocompleteSessionToken = AutocompleteSessionToken.newInstance();
        placesClient =Places.createClient(this);

        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mGps = (ImageView) findViewById(R.id.ic_gps);
        search = (ImageView) findViewById(R.id.ic_search);
        mSave = (ImageView) findViewById(R.id.save_back);
        if(Variables.isInclusive()) {
            mSave.setEnabled(true);
            mSave.setColorFilter(Color.BLACK);
        } else {
            mSave.setEnabled(false);
            mSave.setColorFilter(Color.GRAY);
        }
        mBack = (ImageView) findViewById(R.id.back);

        tts = new TextToSpeech(MapActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    tts.setLanguage(Locale.ENGLISH);
            }
        });

        getLocationPermission();

    }

    private void init(){

        PlaceAutocompleteAdapter mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, placesClient,
                autocompleteSessionToken);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        if (!searchAddress.equals("Set destination")){
            mSearchText.setText(searchAddress);
        }


//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//
//        autocompleteFragment.setTypesFilter(Arrays.asList(TypeFilter.ADDRESS.toString()));
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                geoLocate();
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//            }
//        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Variables.isInclusive()) {
                    String text = "Find current location";
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
                getDeviceLocation();
            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                if (Variables.isInclusive() && (saveAddress != null)) {
                    text = "Save location";
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    Intent intent = new Intent();
                    intent.putExtra("address", saveAddress.getAddressLine(0));
                    setResult(RESULT_OK, intent);
                    finish();
                } else if (mSave.isEnabled() && !Variables.isInclusive()) {
                    Intent intent = new Intent();
                    intent.putExtra("address", saveAddress.getAddressLine(0));
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    text = "Pleas find location";
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Variables.isInclusive()) {
                    String text = "Back";
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
                Intent intent = new Intent();
                intent.putExtra("address", searchAddress);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        hideSoftKeyboard();

    }


    private void geoLocate(){

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){

        }

        if(list.size() > 0){
            Address address = list.get(0);
            saveAddress = address;

            mMap.clear();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,
                    address.getAddressLine(0));

            if (Variables.isInclusive()) {
                String text = address.getAddressLine(0);
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                mSave.setEnabled(true);
                mSave.setColorFilter(Color.BLACK);
            }
        } else {
            if (Variables.isInclusive()) {
                String text = "Location not found";
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            } else {
                mSave.setEnabled(false);
                mSave.setColorFilter(Color.GRAY);
                Toast.makeText(MapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDeviceLocation(){

        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");

                        }else{
                            if (Variables.isInclusive()) {
                                String text = "Unable to get current location";
                                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                            } else {
                                Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }catch (SecurityException e){
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));


        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            if(Variables.isInclusive()) {
                options.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}

