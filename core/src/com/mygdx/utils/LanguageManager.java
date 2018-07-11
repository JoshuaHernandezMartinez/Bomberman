package com.mygdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.Locale;

public class LanguageManager {

    private static ObjectMap<String, I18NBundle> languages;
    private static String currentLanguage;

    private static final LanguageManager ourInstance = new LanguageManager();

    public static LanguageManager getInstance() {
        return ourInstance;
    }

    private LanguageManager(){

        languages = new ObjectMap<String, I18NBundle>();
        currentLanguage = null;
        init();

    }

    private void init(){
        FileHandle languageFileHandle = Gdx.files.internal("language/strings_en_GB");
        loadLanguage("English", languageFileHandle, Locale.UK);

        languageFileHandle = Gdx.files.internal("language/strings_es_ES");
        loadLanguage("Spanish", languageFileHandle,  new Locale("es", "ES"));

        languageFileHandle = Gdx.files.internal("language/strings_pt_BR");
        loadLanguage("Portuguese", languageFileHandle,  new Locale("pt", "BR"));

        languageFileHandle = Gdx.files.internal("language/strings_de_DE");
        loadLanguage("German", languageFileHandle,  new Locale("de", "DE"));

        GamePreferences.instance.load();

        setCurrentLanguage(Languages.values()[GamePreferences.instance.language].name().toLowerCase());
    }


    public void loadLanguage(String name, FileHandle fileHandle, Locale locale){
        if(name != null && !name.isEmpty() && fileHandle != null && locale != null){
            languages.put(name.toLowerCase(), I18NBundle.createBundle(fileHandle, locale));
        }
    }

    public void setCurrentLanguage(String name) {
        if(languages.containsKey(name.toLowerCase()))
            currentLanguage = name;
    }

    public static I18NBundle getCurrentBundle(){
        return languages.get(currentLanguage);
    }

}


