package com.tanganmu.iptv;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

public class SingleRowView {

    String name = "";
    String image = null ;
    String Video = "";
    String Signal = "" ;
    String Category = "";

    public SingleRowView(String name, String image, String video, String signal, String category) {
        this.name = name;
        this.image = image;
        Video = video;
        Signal = signal;
        Category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return Video;
    }

    public void setVideo(String video) {
        Video = video;
    }

    public String getSignal() {
        return Signal;
    }

    public void setSignal(String signal) {
        Signal = signal;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("name" , name);
            obj.put("image" , image);
            obj.put("Video" , Video);
            obj.put("Signal" , Signal);
            obj.put("category" , Category);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return String.valueOf(obj);


    }
}
