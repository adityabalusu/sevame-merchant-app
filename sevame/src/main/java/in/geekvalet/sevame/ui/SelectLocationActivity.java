package in.geekvalet.sevame.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import in.geekvalet.sevame.R;
import in.geekvalet.sevame.libs.KeyValueStore;

import java.util.List;

public class SelectLocationActivity extends MapContainerActivity {
    private static final String LOG_TAG = SelectLocationActivity.class.getName();

    public static final int MARKER_X = 36;
    public static final int MARKER_Y = 128;

    private ImageView marker;
    private Button saveLocationButton;
    private KeyValueStore savedMaps;
    private ViewGroup savedLocations;
    private Boolean updateLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        marker = (ImageView) findViewById(R.id.maps_marker);
        saveLocationButton = (Button) findViewById(R.id.save_location_button);
        savedMaps = new KeyValueStore(this, "SavedMaps");
        savedLocations = (ViewGroup) findViewById(R.id.saved_locations);

        if (getMap() != null) {
            setupSaveLocationButton();
            setupSavedLocations();
            setupEventHandlers();
        }

    }

    private void setupEventHandlers() {
        getMap().setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                updateLocation = false;
            }
        });
    }


    @Override
    protected int getMapId() {
        return R.id.map;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.select_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_send_location:
                sendCurrentLocation();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendCurrentLocation() {
        LatLng latLng = getCurrentLocation();

        final Intent result = new Intent();
        result.putExtra("latLng", latLng);
        setResult(RESULT_OK, result);
        finish();
    }

    private void setupSavedLocations() {
        List<String> locationNames = savedMaps.getKeys();

        for(final String locationName: locationNames) {
            setupSavedLocation(locationName);
        }
    }

    private void setupSavedLocation(final String locationName) {
        Log.d(LOG_TAG, "Showing location " + locationName);
        byte[] data = savedMaps.getBytes(locationName, null);

   /*     if(data != null) {
            Parcel parcel = Parcel.obtain();
            parcel.unmarshall(data, 0, data.length);
            parcel.setDataPosition(0);
            final LatLng latLng = LatLng.CREATOR.createFromParcel(parcel);

            final View view = getLayoutInflater().inflate(R.layout.fragment_saved_location, savedLocations, false);
            TextView name = (TextView) view.findViewById(R.id.location_name);
            Button deleteButton = (Button) view.findViewById(R.id.delete_button);

            name.setText(locationName);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    savedLocations.removeView(view);
                    savedMaps.remove(locationName);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Location location = new Location("");
                    location.setLatitude(latLng.latitude);
                    location.setLongitude(latLng.longitude);

                    setCurrentLocation(location);
                }
            });
            savedLocations.addView(view);
        }*/
    }

    private void setupSaveLocationButton() {
        saveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputCurrentLocationName();
            }
        });
    }

    private void inputCurrentLocationName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String locationName = input.getText().toString();
                saveCurrentLocationAs(locationName);
                setupSavedLocation(locationName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show().getWindow().setLayout(600, 400);
    }

    private void saveCurrentLocationAs(String locationName) {
        LatLng latLng = getCurrentLocation();

        Parcel parcel = Parcel.obtain();
        latLng.writeToParcel(parcel, 0);

        savedMaps.write(locationName, parcel.marshall());
    }

    private LatLng getCurrentLocation() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) marker.getLayoutParams();
        int screenX = params.leftMargin + MARKER_X;
        int screenY = params.topMargin + MARKER_Y;

        return getMap().getProjection().fromScreenLocation(new Point(screenX, screenY));
    }

    protected void setCurrentLocation(Location currentLocation) {
        if(updateLocation) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

            getMap().moveCamera(CameraUpdateFactory.newLatLng(latLng));
            getMap().animateCamera(CameraUpdateFactory.zoomTo(17));

            Point point = getMap().getProjection().toScreenLocation(latLng);

            if(point.x > 0 && point.y > 0) { // This can happen if the layout is still in progress.
                marker.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(128, 128);

                params.leftMargin = point.x - MARKER_X;
                params.topMargin = point.y - MARKER_Y;

                marker.setLayoutParams(params);
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_select_location;
    }
}
