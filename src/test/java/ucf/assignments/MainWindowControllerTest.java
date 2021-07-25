package ucf.assignments;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

class MainWindowControllerTest {

    private ItemModel itemModelTest;
    private MainWindowController controller;

    @BeforeEach
    void init() {
        controller = new MainWindowController();

        itemModelTest = new ItemModel();
        itemModelTest.getItems().add(new Item("ABCDE12345", "MacBook Pro", 1199.00));
        itemModelTest.getItems().add(new Item("QWERTY7890", "AirPods Pro", 199.99));

        System.out.println(itemModelTest.getItems().size());
    }

    @Test
    @DisplayName("Add new Inventory Item test")
    void addItemTest() {
        // Add new item
        controller.addItem(itemModelTest, "AAAAABBBBB", "Apple Watch", 499.00);

        // Check the size increase by one
        assertTrue(itemModelTest.getItems().size() == 3);

        assertTrue(itemModelTest.getItems().get(2).getSerialNumber().equalsIgnoreCase("AAAAABBBBB"));
        assertTrue(itemModelTest.getItems().get(2).getName().equalsIgnoreCase("Apple Watch"));
        assertTrue(itemModelTest.getItems().get(2).getValue() == 499.00);
    }

    @Test
    @DisplayName("Add new Inventory Item test")
    void deleteItem() {
    }

    @Test
    void filterList() {
    }

    @Test
    void searchFindsItem() {
    }

    @Test
    void sortInventoryItems() {
        // Since Table View handles sorting, and it is not required to test with TestFX
        // There is nothing to test for Sorting
    }
}