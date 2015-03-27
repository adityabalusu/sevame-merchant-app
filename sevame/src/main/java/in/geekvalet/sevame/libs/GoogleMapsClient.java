package in.geekvalet.sevame.libs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gautam on 4/6/14.
 */
public class GoogleMapsClient {
    public static Bitmap getThumbnail(double lat, double lng){
        String location = lat + "," + lng;
        String query = new RestClient.QueryParameters()
                .put("center", location)
                .put("zoom", 13)
                .put("size", "100x100")
                .put("sensor", "true")
                .put("maptype", "roadmap")
                .put("markers", location)
                .toQueryString();

        String URL = "http://maps.google.com/maps/api/staticmap?" + query;
        Bitmap bmp = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(URL);

        InputStream in = null;
        try {
            in = httpclient.execute(request).getEntity().getContent();
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bmp;
    }
}
