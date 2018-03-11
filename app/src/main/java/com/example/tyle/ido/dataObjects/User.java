package com.example.tyle.ido.dataObjects;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User {

    public static String email;
    public static String userid;
    public static String name;
    public static ArrayList<ToDoList> lists;

    public User() {

    }

    public User(String name, String email, String userid, ArrayList<ToDoList> lists) {
        User.email = email;
        User.name = name;
        User.lists = lists;
        User.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static String getUserid() {
        return userid;
    }

    public static void setUserid(String userid) {
        User.userid = userid;
    }

    public List<ToDoList> getLists() {
        return lists;
    }

    public void setLists(ArrayList<ToDoList> lists) {
        this.lists = lists;
    }

}
