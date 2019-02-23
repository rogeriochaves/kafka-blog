package com.example.kafkablog;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Post {
    @JsonAlias({ "id", "ID" })
    private String id;
    @JsonAlias({ "title", "TITLE" })
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
