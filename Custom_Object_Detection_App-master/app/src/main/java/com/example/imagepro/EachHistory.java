package com.example.imagepro;

import java.io.Serializable;

public class EachHistory implements Serializable {
    private String text;
    private String added_on;

    public EachHistory() {
    }

    public EachHistory(String text, String added_on) {
        this.text = text;
        this.added_on = added_on;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAdded_on() {
        return added_on;
    }

    public void setAdded_on(String added_on) {
        this.added_on = added_on;
    }
}
