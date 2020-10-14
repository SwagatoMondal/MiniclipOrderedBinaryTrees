package com.miniclip.bstree.util;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONParser {

    @NonNull
    private JSONArray array;

    public JSONParser(@NonNull JSONArray array) {
        this.array = array;
    }

    public int length() {
        return array.length();
    }

    public int getValue(int index) {
        try {
            return array.getJSONObject(index).getInt("value");
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getColor(int index) {
        try {
            return array.getJSONObject(index).getString("color");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
