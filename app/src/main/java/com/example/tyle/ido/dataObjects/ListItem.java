package com.example.tyle.ido.dataObjects;

/**
 * Created by tytusplanck on 10/26/17.
 */

public class ListItem {

    public String name;

    public ListItem(){

    }

    public ListItem(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
