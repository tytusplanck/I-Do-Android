package com.example.tyle.ido.dataObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by tytusplanck on 10/26/17.
 */

public class ListItem implements Parcelable{

    public String name;
    public String cost;

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

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(cost);

    }

    private ListItem(Parcel in) {
        name = in.readString();
        cost = in.readString();
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
