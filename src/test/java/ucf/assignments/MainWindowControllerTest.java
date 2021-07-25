package ucf.assignments;

import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


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
        // Add 4 Inventory Items for testing
        itemModelTest.getItems().add(new Item("ABCDE12345", "MacBook Pro", 1199.00));
        itemModelTest.getItems().add(new Item("QWERTY7890", "AirPods Pro", 199.99));
        itemModelTest.getItems().add(new Item("NIKE123456", "Air Max", 170.00));
        itemModelTest.getItems().add(new Item("ABCDE34567", "Microsoft Surface", 799.99));
    }

    @Test
    @DisplayName("Add new Inventory Item test")
    void addItemTest() {
        // Add new item
        controller.addItem(itemModelTest, "AAAAABBBBB", "Apple Watch", 499.00);

        // Check the size increase by one
        assertEquals(5, itemModelTest.getItems().size());

        // Newly added item will be at index 4
        assertTrue(itemModelTest.getItems().get(4).getSerialNumber().equalsIgnoreCase("AAAAABBBBB"));
        assertTrue(itemModelTest.getItems().get(4).getName().equalsIgnoreCase("Apple Watch"));
        assertTrue(itemModelTest.getItems().get(4).getValue() == 499.00);
    }

    @Test
    @DisplayName("Add new Inventory Item test")
    void deleteItemTest() {
        // Before deleting an Item, check the size is 4
        assertEquals(4, itemModelTest.getItems().size());
        // Check item at index 0 before deleting this item
        assertEquals(itemModelTest.getItems().get(0).getName(), "MacBook Pro");

        // Remove the first item of the list
        controller.deleteItem(itemModelTest, itemModelTest.getItems().get(0));
        // Check the new ItemModel size is now 3
        assertEquals(3, itemModelTest.getItems().size());
        // Check the item at index 0 is now AirPods Pro
        assertEquals(itemModelTest.getItems().get(0).getName(), "AirPods Pro");
    }

    @Test
    @DisplayName("Create new ObservableList<Item> with items that math a search text")
    void filterListTest() {
        ObservableList<Item> list1 = controller.filterList(itemModelTest, "Pro");
        ObservableList<Item> list2 = controller.filterList(itemModelTest, "Nike");

        // Assert that two items of the itemModelTest contains the search text "Pro"
        assertEquals(2, list1.size());
        // Assert that one items of the itemModelTest contains the search text "Nike"
        assertEquals(1, list2.size());
    }

    @Test
    @DisplayName("Check if a given Item contains a search text in the serial # or name")
    void searchFindsItemTest() {
        // Check if item at index 0 contains "Mac"
        assertTrue(controller.searchFindsItem(itemModelTest.getItems().get(0), "Mac"));
        // Check if item at index 0 contains "AA"
        assertFalse(controller.searchFindsItem(itemModelTest.getItems().get(0), "AA"));
        // Check if item at index 0 contains "12345"
        assertTrue(controller.searchFindsItem(itemModelTest.getItems().get(0), "12345"));
    }

    @Test
    void sortInventoryItemsTest() {
        // Since Table View handles sorting, and it is not required to test with TestFX
        // There is nothing to test for Sorting
    }
}