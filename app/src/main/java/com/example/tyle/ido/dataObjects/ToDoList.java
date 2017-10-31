package com.example.tyle.ido.dataObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tytusplanck on 10/26/17.
 */

public class ToDoList {

    public String name;
    public String description;
    public ArrayList<ListItem> toDoList = new ArrayList<>();

    public ToDoList(){

    }

    public ToDoList(String name, String description, ArrayList<ListItem> toDoList){
        this.toDoList = toDoList;
        this.description = description;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ListItem> getToDoList() {
        return toDoList;
    }

    public void setToDoList(ArrayList<ListItem> toDoList) {
        this.toDoList = toDoList;
    }
}
