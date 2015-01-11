package in.geekvalet.sevame.libs;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestClient {

    public static class QueryParameters {
        private Map<String, String> params = new HashMap<String, String>();

        public QueryParameters put(String key, Object value) {
            params.put(key, value.toString());
            return this;
        }

        public Map<String, String> getParams() {
            return params;
        }

        public String toQueryString() {
            try {
                return mapToQueryString(params);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    private static final String LOG_TAG = RestClient.class.getName();

    public static class UnsuccessfulRestCall extends RuntimeException {
        private final HttpResponse response;
        private final HttpRequestBase request;

        public UnsuccessfulRestCall(HttpRequestBase request, HttpResponse response) {
            super(request.getURI() + " returned " + response.getStatusLine().getStatusCode());
            this.response = response;
            this.request = request;
        }

        public HttpResponse getResponse() {
            return response;
        }

        public HttpRequestBase getRequest() {
            return request;
        }
    }

    public static String post(String url, String body) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(asStringEntity(body));
        return execute(httpPost);
    }


    public static String postData(String url, Map parameters) {
        HttpPost httppost = new HttpPost(url);
        try {
            httppost.setEntity(new UrlEncodedFormEntity(mapToNameValuePairs(parameters)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return execute(httppost);
    }

    public static String get(String url) {
        return execute(new HttpGet(url));
    }

    public static String get(String url, Map<String, String> queryParameters) {
        try {
            return get(url + mapToQueryString(queryParameters));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String get(String url, QueryParameters queryParameters) {
        return get(url, queryParameters.getParams());
    }

    public static String put(String url, String body) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPut httpPut = new HttpPut(url);
        try {
            httpPut.setEntity(new StringEntity(body));

            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");

            HttpResponse response = httpclient.execute(httpPut);
            return EntityUtils.toString(response.getEntity());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String delete(String url) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpDelete httpDelete = new HttpDelete(url);
        try {
            httpDelete.setHeader("Accept", "application/json");
            httpDelete.setHeader("Content-type", "application/json");

            HttpResponse response = httpclient.execute(httpDelete);
            return EntityUtils.toString(response.getEntity());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static String mapToQueryString(Map<String, String> queryParameters) throws UnsupportedEncodingException {
        StringBuilder string = new StringBuilder();

        if(queryParameters.size() > 0) {
            string.append("?");
        }

        for(Map.Entry<String, String> entry : queryParameters.entrySet()) {
            string.append(entry.getKey());
            string.append("=");
            string.append(URLEncoder.encode(entry.getValue(), "utf-8"));
            string.append("&");
        }

        return string.toString();
    }


    private static String execute(HttpRequestBase request) {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        Log.d(LOG_TAG, "Executing " + request.getMethod() + " " + request.getURI());
        try {
            HttpResponse response = httpClient.execute(request);

            if(isSuccessful(response)) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new UnsuccessfulRestCall(request, response);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static boolean isSuccessful(HttpResponse response) {
        int code = response.getStatusLine().getStatusCode();

        return code >= 200 && code < 300;
    }

    private static HttpEntity asStringEntity(String body) {
        try {
            return new StringEntity(body);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static List<NameValuePair> mapToNameValuePairs(Map parameters) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        for(Object key: parameters.keySet()) {
            nameValuePairs.add(new BasicNameValuePair(key.toString(), parameters.get(key).toString()));
        }
        return nameValuePairs;
    }
}
