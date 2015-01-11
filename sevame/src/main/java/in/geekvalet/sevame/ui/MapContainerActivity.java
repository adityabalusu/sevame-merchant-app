package in.geekvalet.sevame.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by gautam on 2/6/14.
 */
public abstract class MapContainerActivity extends ActionBarActivity {
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final long UPDATE_INTERVAL = 5000;
    private static final int FASTEST_INTERVAL = 1000;
    private static final String LOG_TAG = MapContainerActivity.class.getName();

    private GoogleMap map;
    private LocationClient locationClient;
    private LocationRequest locationRequest;
    private boolean locationInitialized = false;

    private SupportMapFragment mapFragment;

    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    protected class GooglePlayConnectionHandler implements
            GooglePlayServicesClient.ConnectionCallbacks,
            GooglePlayServicesClient.OnConnectionFailedListener {

        @Override
        public void onConnected(Bundle bundle) {
            initializeCurrentLocation();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(MapContainerActivity.this, "Disconnected from the internet. Please re-connect.",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (connectionResult.hasResolution()) {
                try {
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(
                            MapContainerActivity.this,
                            CONNECTION_FAILURE_RESOLUTION_REQUEST);

                } catch (IntentSender.SendIntentException e) {
                    // Log the error
                    e.printStackTrace();
                }
            } else {
                showErrorDialog(connectionResult.getErrorCode());
            }

        }
    }

    class LocationListener implements com.google.android.gms.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            setCurrentLocation(location);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
                switch (resultCode) {
                    case Activity.RESULT_OK :
                        break;
                }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(LOG_TAG, "Google Play services is available.");
            return true;
        } else {
            showErrorDialog(resultCode);
            return false;
        }
    }


    private void showErrorDialog(int errorCode) {
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
            // Create a new DialogFragment for the error dialog
            ErrorDialogFragment errorFragment =
                    new ErrorDialogFragment();
            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(getSupportFragmentManager(),
                    "Location Updates");
        }
    }

    private void setupLocationRequest() {
        locationRequest = LocationRequest.create();

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        if (map == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(getMapId());
            map = mapFragment.getMap();

            // Check if we were successful in obtaining the map.
            if (map != null) {
                map.setMyLocationEnabled(true);
            }

            if(requireCurrentLocation()) {
                GooglePlayConnectionHandler googlePlayConnectionHandler = new GooglePlayConnectionHandler();
                locationClient = new LocationClient(this , googlePlayConnectionHandler, googlePlayConnectionHandler);
                initializeCurrentLocation();
                setupLocationRequest();
                registerLayoutListener();
            }
        }
    }

    // Workaround for the scenario when the current location is recevied before layout is complete:
    // Attempt to intialize the current location once layout is done
    private void registerLayoutListener() {
        final View mapView = mapFragment.getView();
        if (mapView.getViewTreeObserver().isAlive()) {
            mapView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    initializeCurrentLocation();
                }
            });
        }
    }

    private void initializeCurrentLocation() {
        if(isGooglePlayServicesAvailable() && locationClient.isConnected()) {
            Location currentLocation = locationClient.getLastLocation();

            if(!locationInitialized) {
                setCurrentLocation(currentLocation);
                locationClient.requestLocationUpdates(locationRequest, new LocationListener());
                locationInitialized = true;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(requireCurrentLocation()) {
            locationClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if(requireCurrentLocation()) {
            locationClient.disconnect();
        }

        super.onStop();
    }

    public SupportMapFragment getMapFragment() {
        return mapFragment;
    }

    public GoogleMap getMap() {
        return map;
    }

    protected abstract int getMapId();
    protected abstract int getLayoutResource();


    protected void setCurrentLocation(Location location) {

    }

    protected boolean requireCurrentLocation() {
        return true;
    }
}
