package com.example.akshiban.everything.notes;

import java.util.ArrayList;

public class KV {
    String key;
    long dateCreated, dateModified;
    String value;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ArrayList<String> getKeys(ArrayList<KV> semesters) {
        ArrayList<String> list = new ArrayList<>();
        for(KV kv: semesters) list.add(kv.getKey());
        return list;
    }
}
