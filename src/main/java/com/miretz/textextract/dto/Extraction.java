package com.miretz.textextract.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
public class Extraction {

    private String extractedText;
    private String language;
    private Map<String, String> metadata = new HashMap<>();


    public Extraction() {
        //JACKSON PURPOSE
    }

    public Extraction(String extractedText, String language, Map<String, String> metadata) {
        this.extractedText = extractedText;
        this.language = language;
        this.metadata = metadata;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
