package com.ramyfradwan.themovieapp.Model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RamyFRadwan on 08/10/15.
 */
public class Trailer {
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;

    public Trailer(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
        this.site = trailer.getString("site");
        this.type = trailer.getString("type");
        Log.v("JSON Trailer Data" , this.id+ ",,"+this.name+ ",,"+this.site+ ",,"+this.key+ ",,"+ this.type);

    }

    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        Log.v("Trailer getKey",""+ key);
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }

}
