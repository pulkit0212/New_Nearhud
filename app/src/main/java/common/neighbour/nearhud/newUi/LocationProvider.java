package common.neighbour.nearhud.newUi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;

import static java.sql.DriverManager.println;

public class LocationProvider implements LifecycleObserver {

    private EasyLocationCallback callback;
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private long interval;
    private long fastestInterval;
    private int priority;
    private int numberOfUpdates;
    private double Latitude = 0.0, Longitude = 0.0;

    private LocationProvider(final Builder builder) {
        context = builder.context;
        callback = builder.callback;
        interval = builder.interval;
        fastestInterval = builder.fastestInterval;
        priority = builder.priority;
        numberOfUpdates = builder.numberOfUpdates;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private void connectGoogleClient() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient.connect();
        } else {
            int REQUEST_GOOGLE_PLAY_SERVICE = 988;
            googleAPI.getErrorDialog((AppCompatActivity) context, resultCode, REQUEST_GOOGLE_PLAY_SERVICE);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    private void onCreateLocationProvider() {
         println("on create");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onLocationResume() {
        println("on resume");
        buildGoogleApiClient();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void removeObserver(){
        println("destroy");
        removeUpdates();
    }

    @SuppressLint("MissingPermission")
    private synchronized void buildGoogleApiClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        callback.onGoogleAPIClient(mGoogleApiClient, "Connected");

                        mLocationRequest = new LocationRequest();
                        mLocationRequest.setInterval(interval);
                        mLocationRequest.setFastestInterval(fastestInterval);
                        mLocationRequest.setPriority(priority);
                        mLocationRequest.setSmallestDisplacement(0);
                        if (numberOfUpdates > 0) mLocationRequest.setNumUpdates(numberOfUpdates);

                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
                        builder.addLocationRequest(mLocationRequest);
                        builder.setAlwaysShow(true);
                        mLocationSettingsRequest = builder.build();

                        mSettingsClient
                                .checkLocationSettings(mLocationSettingsRequest)
                                .addOnSuccessListener(locationSettingsResponse -> {
                                    showLog("GPS is Enabled Requested Location Update");
                                    requestLocationUpdate();
                                }).addOnFailureListener(e -> {
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        int REQUEST_CHECK_SETTINGS = 214;
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult((AppCompatActivity) context, REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException sie) {
                                        showLog("Unable to Execute Request");
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    showLog("Location Settings are Inadequate, and Cannot be fixed here. Fix in Settings");
                            }
                        }).addOnCanceledListener(new OnCanceledListener() {
                            @Override
                            public void onCanceled() {
                                showLog("onCanceled");
                            }
                        }).addOnFailureListener(e -> {
                            e.printStackTrace();
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        connectGoogleClient();
                        callback.onGoogleAPIClient(mGoogleApiClient, "Connection Suspended");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        callback.onGoogleAPIClient(mGoogleApiClient, "" + connectionResult.getErrorCode() + " " + connectionResult.getErrorMessage());
                    }
                })
                .addApi(LocationServices.API)
                .build();

        connectGoogleClient();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Latitude = locationResult.getLastLocation().getLatitude();
                Longitude = locationResult.getLastLocation().getLongitude();

                if (Latitude == 0.0 && Longitude == 0.0) {
                    showLog("New Location Requested");
                    requestLocationUpdate();
                } else {
                    callback.onLocationUpdated(Latitude, Longitude);
                }
            }
        };
    }

    /*public LatLng getLastLocation() {
        if ( ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            Location location = mFusedLocationClient.getLastLocation().getResult();
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
        return null;
    }*/

    @SuppressLint("MissingPermission")
    public void removeUpdates() {
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            callback.onLocationUpdateRemoved();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLog(String message) {
        Log.e("LocationProvider", "" + message);
    }

    public interface EasyLocationCallback {
        void onGoogleAPIClient(GoogleApiClient googleApiClient, String message);

        void onLocationUpdated(double latitude, double longitude);

        void onLocationUpdateRemoved();
    }

    public static class Builder {
        private Context context;
        private EasyLocationCallback callback;
        private long interval = 10 * 1000;
        private long fastestInterval = 5 * 1000;
        private int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        private int numberOfUpdates = 0;

        public Builder(Context context) {
            this.context = context;
        }

        public LocationProvider build() {
            if (callback == null) {
                Toast.makeText(context, "EasyLocationCallback listener can not be null", Toast.LENGTH_SHORT).show();
            }

            return new LocationProvider(this);
        }

        public Builder setListener(EasyLocationCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder setInterval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder setFastestInterval(int fastestInterval) {
            this.fastestInterval = fastestInterval;
            return this;
        }

        public Builder setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder setNumberOfUpdates(int numberOfUpdates) {
            this.numberOfUpdates = numberOfUpdates;
            return this;
        }
    }
}
