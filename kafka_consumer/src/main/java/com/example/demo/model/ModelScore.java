package com.example.demo.model;

import java.util.Map;
import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class ModelScore implements Serializable {
    @SerializedName("ID")
    private String id;
    private Map<String, String> scores;

    // create method getScore
    public Map<String, String> getScore() {
        return scores;
    }

    // create method getId
    public String getId() {
        return id;
    }
}