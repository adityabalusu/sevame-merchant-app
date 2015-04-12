package in.geekvalet.sevame.httpClient;


import android.net.Uri;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import in.geekvalet.sevame.Application;

public class HttpClientM {

    //final String sevameURI = "https://sevame.in/api";
    public String get(String uri, String request, Map<String, String> qParam, Map<String, String> pathVar) {
        HttpClient httpClient = new DefaultHttpClient();

      /*  Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("sevame.in/api").appendPath(uri);
        String url = builder.build().toString();*/
        HttpGet httpGet = new HttpGet("https://sevame.in/api/" + uri);
        String authCode = Application.getDataStore().getAuthToken();
        httpGet.setHeader("X-Session-Id", authCode);
        String entity = null;
        try {

            HttpResponse response = httpClient.execute(httpGet); // execute httpGet
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

                HttpEntity e = response.getEntity();
                entity = EntityUtils.toString(e);
;
        } catch ( Exception e) {
            return null;
        }
         /*   e.printStackTrace();*/
   /*     } catch (Exception e) {
            return null;

        }*/
        return entity;

    }



    public String post(String uri, String request, Map<String, String>qParam, Map<String, String> pathVar){
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;

        HttpPost httpPost = new HttpPost("https://sevame.in/api/"+uri);
        String authCode = Application.getDataStore().getAuthToken();
        httpPost.setHeader("X-Session-Id", authCode);
        try {
        StringEntity se = new StringEntity(request);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(se);
            response = client.execute(httpPost);
        } catch(Exception e) {
            return null;
            //Toast.makeText("Erroe", "Error", "Cannot Estabilish Connection", );
        }


        return null;
    }

    public String put(String uri, String request, Map<String, String>qParam, Map<String, String> pathVar){

        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;

        HttpPut httpPut = new HttpPut("https://sevame.in/api/" + uri);
        String authCode = Application.getDataStore().getAuthToken();
        httpPut.setHeader("X-Session-Id", authCode);
        String repJson = null;

        try {

            StringEntity se = new StringEntity(request);
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPut.setEntity(se);
            response = client.execute(httpPut);

            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                repJson = EntityUtils.toString(entity);
            }
            return  repJson;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
            //Toast.makeText("Erroe", "Error", "Cannot Estabilish Connection", );
        }
    }




    public String delete(String uri, String request, Map<String, String>qParam, Map<String, String> pathVar){

        return null;
    }

    private String constructUri(String uri, Map<String, String>qParam, Map<String, String> pathVar){


        return null;
    }


}
