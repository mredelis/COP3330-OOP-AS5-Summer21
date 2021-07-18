package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;

public class ItemModel implements Serializable {

    public ObservableList<Item> items;

    public ItemModel() {
        this.items = FXCollections.observableArrayList();
    }

    public ItemModel(ObservableList<Item> items) {
        this.items = items;
    }

    public ObservableList<Item> getItems() {
        return items;
    }
}
