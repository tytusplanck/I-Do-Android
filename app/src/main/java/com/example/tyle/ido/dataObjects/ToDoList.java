package com.example.tyle.ido.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tytusplanck on 10/26/17.
 */

public class ToDoList implements Parcelable{

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


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(description);
        out.writeTypedList(toDoList);
    }

    private ToDoList(Parcel in) {
        name = in.readString();
        description = in.readString();
        toDoList = in.readArrayList(ToDoList.class.getClassLoader());
    }

    @Override
    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ToDoList createFromParcel(Parcel in) {
            return new ToDoList(in);
        }

        public ToDoList[] newArray(int size) {
            return new ToDoList[size];
        }
    };

}
