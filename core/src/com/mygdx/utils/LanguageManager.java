package com.mygdx.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Locale;

public class LanguageManager {

    private ObjectMap<String, I18NBundle> languages;
    private String currentLanguage;

    public LanguageManager(){

        languages = new ObjectMap<String, I18NBundle>();
        currentLanguage = null;
    }

    public void loadLanguage(String name, I18NBundle bundle){

        if(name != null && !name.isEmpty() && bundle != null){
            languages.put(name.toLowerCase(), bundle);
        }

    }

    public void loadLanguage(String name, FileHandle fileHandle, Locale locale){
        if(name != null && !name.isEmpty() && fileHandle != null && locale != null){
            languages.put(name.toLowerCase(), I18NBundle.createBundle(fileHandle, locale));
        }
    }

    public String getCurrentLanguage(){
        return currentLanguage;
    }

    public void setCurrentLanguage(String name) {
        if(languages.containsKey(name.toLowerCase()))
            currentLanguage = name;
    }

    public I18NBundle getCurrentBundle(){
        return languages.get(currentLanguage);
    }

}


