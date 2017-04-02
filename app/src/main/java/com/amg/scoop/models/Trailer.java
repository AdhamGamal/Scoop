package com.amg.scoop.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Trailer {

    private String id;
    private String key;
    private String name;

    public Trailer(JSONObject trialers_json) {
        try {
            this.id = trialers_json.getString("id");
            this.key = trialers_json.getString("key");
            this.name = trialers_json.getString("name");
        } catch (JSONException e) {
        }
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

}
