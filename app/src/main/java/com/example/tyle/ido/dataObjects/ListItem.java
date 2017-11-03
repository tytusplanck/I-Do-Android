package com.example.tyle.ido.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by tytusplanck on 10/26/17.
 */

public class ListItem implements Parcelable{

    public String name;
    public double cost;
    public int isCompleted = 0;

    public ListItem(){

    }

    public ListItem(String name, double cost, int isCompleted){
        this.name = name;
        this.cost = cost;
        this.isCompleted = isCompleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeDouble(cost);

    }

    private ListItem(Parcel in) {
        name = in.readString();
        cost = in.readDouble();
    }

    public int describeContents() {
        return this.hashCode();
    }

    public static final Parcelable.Creator<ListItem> CREATOR =
            new Parcelable.Creator<ListItem>() {
                public ListItem createFromParcel(Parcel in) {
                    return new ListItem(in);
                }

                public ListItem[] newArray(int size) {
                    return new ListItem[size];
                }
            };

}
