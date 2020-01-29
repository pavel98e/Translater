package com.elshin.translater.models;

import io.realm.RealmList;
import io.realm.RealmObject;

public class DirTranslation extends RealmObject {
    private String dir;
    private RealmList<Translation> translations;

    public DirTranslation(){

    }

    public DirTranslation(String dir){
        this.dir = dir;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public RealmList<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(RealmList<Translation> translations) {
        this.translations = translations;
    }
}
