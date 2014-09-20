package com.ravcode.imagesearch.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ravi on 9/18/14.
 */
public class Image implements Serializable {

    public String fullURL;
    public String thumbnailURL;
    public String title;
    public int width;
    public int height;

    public Image(JSONObject imageJSON) {
        try {
            fullURL = imageJSON.getString("url");
            thumbnailURL = imageJSON.getString("tbUrl");
            title = imageJSON.getString("titleNoFormatting");
            width = imageJSON.getInt("width");
            height = imageJSON.getInt("height");
        } catch (JSONException e) {
            // Invalid JSON
            e.printStackTrace();
        }
    }

    public static ArrayList<Image> fromJSONArray(JSONArray resultsJSONArray) {
        ArrayList<Image> images = new ArrayList<Image>();
        try {
            for (int i = 0; i < resultsJSONArray.length(); i++) {
                images.add(new Image(resultsJSONArray.getJSONObject(i)));
            }
        }
        catch (JSONException e) {
            // Invalid JSON
            e.printStackTrace();
        }
        return images;
    }
}
