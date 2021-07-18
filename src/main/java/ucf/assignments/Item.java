package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class Item {
    SimpleStringProperty name;
    SimpleStringProperty serialNumber;
    SimpleDoubleProperty value;

    public Item(String name, String serialNumber, Double value) {
        this.name = new SimpleStringProperty(name);
        this.serialNumber = new SimpleStringProperty(serialNumber);
        this.value = new SimpleDoubleProperty(value);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public String getSerialNumber() {
        return serialNumber.get();
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = new SimpleStringProperty(serialNumber);
    }

    public double getValue() {
        return value.get();
    }

    public void setValue(Double value) {
        this.value = new SimpleDoubleProperty(value);
    }

}
