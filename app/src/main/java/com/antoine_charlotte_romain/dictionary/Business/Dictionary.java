package com.antoine_charlotte_romain.dictionary.Business;

/**
 * Created by summer1 on 22/06/2015.
 * Updated by summer3 on 22/06/2015.
 */
public class Dictionary {

    private long id;
    private String title;

    public Dictionary (String title){
        this.title = title;
    }

    public Dictionary (long id, String title){
        this.title = title;
        this.id = id;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return this.id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }
}
