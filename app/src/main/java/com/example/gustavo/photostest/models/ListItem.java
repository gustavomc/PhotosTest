package com.example.gustavo.photostest.models;

import com.example.gustavo.photostest.PhotosTestApplication;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmObject;

/**
 * Created by gustavomedina on 11/04/17.
 */

public class ListItem extends RealmObject implements Serializable {

    @SerializedName("id")
    public String id;
    @SerializedName("increment")
    public int increment;
    @SerializedName("name")
    public String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //metodo para obtener el siguiente indice
    public static int getNextKey() {
        try {
            List<ListItem> items = PhotosTestApplication.getRealmInstance().where(ListItem.class).findAll();
            if(items.size() != 0) {
                Integer item = PhotosTestApplication.getRealmInstance().where(ListItem.class).max("increment").intValue();
                if (item != null) {
                    return PhotosTestApplication.getRealmInstance().where(ListItem.class).max("increment").intValue() + 1;
                }
            }
            return 1;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //metodo para obtener todos los elementos de la tabla
    public static List<ListItem> getAll() {
        return PhotosTestApplication.getRealmInstance().where(ListItem.class).findAll();
    }
}
