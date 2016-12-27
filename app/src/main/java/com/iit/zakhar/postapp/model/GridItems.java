package com.iit.zakhar.postapp.model;

import java.io.Serializable;

public class GridItems implements Serializable {

    private String id;
    private String title;

    public GridItems(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
