package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

public class InputValidator {

    public boolean isValidName(String name) {
        return (name.length() > 1 && name.length() < 257);
    }

    public boolean isValidSerialNumber(String serialNumber) {
        return (serialNumber.matches("[0-9A-Z]+") && serialNumber.length() == 10);
    }

    public boolean containsSerialNumber(ItemModel itemModel, String serialNumber) {
        for (int i = 0; i < itemModel.getItems().size(); i++) {
            if (itemModel.getItems().get(i).getSerialNumber().equalsIgnoreCase(serialNumber)) {
                return true;
            }
        }
        return false;
    }
}
