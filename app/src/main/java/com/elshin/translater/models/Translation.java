package com.elshin.translater.models;

import io.realm.RealmObject;

public class Translation extends RealmObject {
    private String inputText;
    private String translatedText;
    private DirTranslation dirTranslation;

    public Translation(){
    }

    public Translation(String inputText, String translatedText, String dir){
        this.inputText = inputText;
        this.translatedText = translatedText;
        this.dirTranslation = new DirTranslation(dir);
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public DirTranslation getDirTranslation() {
        return dirTranslation;
    }

    public void setDirTranslation(DirTranslation dirTranslation) {
        this.dirTranslation = dirTranslation;
    }
}
