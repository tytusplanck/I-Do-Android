package com.example.tyle.ido.dataObjects;

import java.util.ArrayList;
import java.util.List;

public class User {

    public String username;
    public String email;
    public ArrayList<ToDoList> lists;

    public User() {

    }

    public User(String username, String email, ArrayList<ToDoList> lists) {
        this.email = email;
        this.username = username;
        this.lists = lists;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ToDoList> getLists() {
        return lists;
    }

    public void setLists(ArrayList<ToDoList> lists) {
        this.lists = lists;
    }

}
