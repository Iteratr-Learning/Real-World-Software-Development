package com.iteratrlearning.shu_book.chapter_05;

import java.util.HashMap;
import java.util.Map;

public class Facts {

    private Map<String, String> facts = new HashMap<>();

    public String getFact(String name) {
        return this.facts.get(name);
    }

    public void setFact(String name, String value) {
        this.facts.put(name, value);
    }

}
