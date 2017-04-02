package com.amg.scoop.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Review {

    private String id;
    private String author;
    private String content;

    public Review(JSONObject reviews_json) {
        try {
            this.id = reviews_json.getString("id");
            this.author = reviews_json.getString("author");
            this.content = reviews_json.getString("content");
        } catch (JSONException e) {
        }
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }


}
