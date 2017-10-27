package com.example.tyle.ido.dataObjects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tytusplanck on 10/26/17.
 */

public class ToDoList {

    public String name;
    public List<ListItem> toDoList = new ArrayList<>();

    public ToDoList(){

    }

    public ToDoList(String name, List<ListItem> toDoList){
        this.toDoList = toDoList;
        this.name = name;
    }
}
