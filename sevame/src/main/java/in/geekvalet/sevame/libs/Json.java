package in.geekvalet.sevame.libs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class Json {
    private Object object = null;

    public Json(String json) {
        try {
            object = new JSONObject(json);
        } catch (JSONException e) {
            try {
                object = new JSONArray(json);
            } catch (JSONException e1) {
                throw new RuntimeException(e1);
            }
        }
    }

    public Json(Object o) {
        this.object = o;
    }

    public List<Json> asList() {
        try {
            List<Json> list = new ArrayList<Json>();

            if(!(object instanceof JSONArray)) {
                throw new RuntimeException("Invalid json structure while trying to iterate");
            }

            JSONArray jsonArray = (JSONArray) object;

            for(int i = 0; i < jsonArray.length(); i++) {
                list.add(new Json(jsonArray.get(i)));
            }

            return list;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Json> asMap() {
        try {
            Map<String, Json> map = new HashMap<String, Json>();

            if(!(object instanceof JSONObject)) {
                throw new RuntimeException("Invalid json structure while trying cast to map");
            }

            JSONObject jsonObject = (JSONObject) object;

            Iterator iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                map.put(key, new Json(jsonObject.get(key)));
            }

            return map;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String asString() {
        if(!(object instanceof String)) {
            throw new RuntimeException("Invalid json structure while trying cast to string");
        }

        return (String) object;
    }

    public Double asDouble() {
        if(!(object instanceof Double)) {
            throw new RuntimeException("Invalid json structure while trying cast to double");
        }

        return (Double) object;
    }
}
