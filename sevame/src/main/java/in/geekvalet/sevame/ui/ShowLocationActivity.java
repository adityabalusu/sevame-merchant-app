package in.geekvalet.sevame.ui;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import in.geekvalet.sevame.R;

public class ShowLocationActivity extends MapContainerActivity {

    private Location currentLocation;
    private LatLng latLng;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        latLng = getIntent().getParcelableExtra("latLng");
        map = getMap();

        if (map != null) {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            map.addMarker(markerOptions);

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
    }

    @Override
    protected int getMapId() {
        return R.id.map;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_show_location;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_show_directions) {
            launchMaps();
            return true;
        }

        return  super.onOptionsItemSelected(item);
    }

    private void launchMaps() {
        String uri = "https://maps.google.com/maps?f=d&daddr=" + latLng.latitude + "," + latLng.longitude;
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(i);
    }

    @Override
    protected boolean requireCurrentLocation() {
        return false;
    }
}
