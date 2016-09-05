package com.holygon.dishcuss.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.holygon.dishcuss.Activities.RestaurantDetailActivity;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class NearbyFragmentGoogleMap extends Fragment implements
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback {

    AppCompatActivity activity;
    Realm realm;

    public static GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    public static Marker mCurrLocation;
    public static ArrayList<Location> locations=new ArrayList<>();
    public static ArrayList<String> restaurantName=new ArrayList<>();
    public static ArrayList<Integer> restaurantID=new ArrayList<>();


    public NearbyFragmentGoogleMap(){
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.support_map_fragment, container, false);
        activity = (AppCompatActivity) getActivity();

        mFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mFragment.getMapAsync(this);

//        Location location=new Location("");
//        location.setLatitude(32.2);
//        location.setLongitude(74.3);
//        NearbyFragmentGoogleMap.locations.add(location);

//        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                int resID= Integer.parseInt(marker.getTitle());
//                Intent intent=new Intent(getActivity(), RestaurantDetailActivity.class);
//                intent.putExtra("RestaurantID",resID);
//                getActivity().startActivity(intent);
//
//
//            }
//        });



        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {


        mGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);

        mGoogleMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
    }

    public static void DrawMarkers(ArrayList<Location> mLocations){


        LatLng latLng;
        for (int loc=0;loc<mLocations.size();loc++) {
            latLng = new LatLng(mLocations.get(loc).getLatitude(), mLocations.get(loc).getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(restaurantName.get(loc));
//            markerOptions.title(restaurantID.get(loc)+"");
            markerOptions.snippet(restaurantID.get(loc)+"");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mCurrLocation = mGoogleMap.addMarker(markerOptions);
        }

        LatLng currentLocation= new LatLng(NearbyFragmentSearch.mCurrentLocation.getLatitude(),NearbyFragmentSearch.mCurrentLocation.getLongitude());
        float zoomLevel = 10;
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

//        int resID= Integer.parseInt(marker.getTitle());
        int resID= Integer.parseInt(marker.getSnippet());
        Intent intent=new Intent(getActivity(), RestaurantDetailActivity.class);
        intent.putExtra("RestaurantID",resID);
        getActivity().startActivity(intent);
        return false;
    }
}
