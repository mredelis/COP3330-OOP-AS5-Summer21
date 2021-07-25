package ucf.assignments;

import org.junit.Before;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

class UpdateItemControllerTest {

    private UpdateItemController updateItemController;
    private ItemModel itemModelTest;
    private Item selectedItemTest;

    @Test
    void editItemTest() {
        // Create an Item
        selectedItemTest = new Item("AAAAA12345", "Old Name", 399.00);

        // Add created Item to ItemModel
        itemModelTest = new ItemModel();
        itemModelTest.getItems().add(selectedItemTest);

        updateItemController = new UpdateItemController(itemModelTest, selectedItemTest);

        // Update selectedItem
        updateItemController.editItem("BBBBB67890", "New Name", 599.00);

        assertTrue(selectedItemTest.getSerialNumber().equalsIgnoreCase("BBBBB67890"));
        assertTrue(selectedItemTest.getName().equalsIgnoreCase("New Name"));
        assertTrue(selectedItemTest.getValue() == 599.00);
    }
}