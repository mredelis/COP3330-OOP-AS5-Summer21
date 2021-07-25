package ucf.assignments;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

class FileManagerTest {

    private FileManager fileManager;
    private ItemModel itemModelTest;

    @BeforeEach
    void init() {
        fileManager = new FileManager();

        itemModelTest = new ItemModel();
        // Add 4 Inventory Items for testing
        itemModelTest.getItems().add(new Item("ABCDE12345", "MacBook Pro", 1199.00));
        itemModelTest.getItems().add(new Item("QWERTY7890", "AirPods Pro", 199.99));
        itemModelTest.getItems().add(new Item("NIKE123456", "Air Max", 170.00));
        itemModelTest.getItems().add(new Item("ABCDE34567", "Microsoft Surface", 799.99));
    }

    @Test
    void loadInventoryAsTSVTest() {
        // Load a TSV file of Inventory Items
        List<Item> tsvList = fileManager.loadInventoryAsTSV(new File("inputFiles/TSV.txt"));

        assertEquals(2, tsvList.size());
        assertEquals("Item1", tsvList.get(0).getName());
        assertEquals("Item2", tsvList.get(1).getName());
    }

    @Test
    void loadInventoryAsHTMLTest() {
        // Load a HTML file of Inventory Items
        List<Item> htmlList = fileManager.loadInventoryAsHTML(new File("inputFiles/HTML.html"));

        assertEquals(2, htmlList.size());
        assertEquals("Item1", htmlList.get(0).getName());
        assertEquals("Item2", htmlList.get(1).getName());
    }

    @Test
    void loadInventoryAsJSONTest() {
        // Load a JSON file of Inventory Items
        List<Item> jsonList = fileManager.loadInventoryAsJSON(new File("inputFiles/JSON.json"));

        assertEquals(2, jsonList.size());
        assertEquals("Item1", jsonList.get(0).getName());
        assertEquals("Item2", jsonList.get(1).getName());
    }

    @Test
    void readStringTest() {
        String expected = "[{\"sNumber\":\"1234567890\",\"name\":\"Item1\",\"price\":100.0},{\"sNumber\":\"ABCDEFGHIJ\",\"name\":\"Item2\",\"price\":200.0}]";

        String actual = fileManager.readString(new File("inputFiles/JSON.json"));

        assertEquals(expected, actual);
    }

    @Test
    void saveInventoryAsTSVTest() {
        File outFile = new File("outputFiles/TSVOutput.txt");

        // Copy Inventory Items to a TSV file
        fileManager.saveInventoryAsTSV(outFile, itemModelTest);

        // Read in the Content of the file
        String actual = fileManager.readString(outFile);

        String expected = "ABCDE12345\tMacBook Pro\t1199.0" +
                          "QWERTY7890\tAirPods Pro\t199.99" +
                          "NIKE123456\tAir Max\t170.0" +
                          "ABCDE34567\tMicrosoft Surface\t799.99";
        assertEquals(expected, actual);
    }

    @Test
    void saveInventoryAsHTMLTest() {
        File outFile = new File("outputFiles/HTMLOutput.html");

        // Copy Inventory Items to a html file
        fileManager.saveInventoryAsHTML(outFile, itemModelTest);

        // Read in the Content of the file
        String actual = fileManager.readString(outFile);
        String expected = "<!DOCTYPE html><html><head>  <title>OOP Assignment 5</title><style>table {  font-family: arial, sans-serif;  border-collapse: collapse;  width: 40%;}td, th {  border: 1px solid #dddddd;  text-align: left;  padding: 8px;}</style></head><body><h2>Inventory Tracker</h2><table>  <tr>    <th>Serial Number</th>    <th>Name</th>    <th>Value in dollars</th>  </tr>  <tr>    <td>ABCDE12345</td>    <td>MacBook Pro</td>    <td>1199.0</td>  </tr>  <tr>    <td>QWERTY7890</td>    <td>AirPods Pro</td>    <td>199.99</td>  </tr>  <tr>    <td>NIKE123456</td>    <td>Air Max</td>    <td>170.0</td>  </tr>  <tr>    <td>ABCDE34567</td>    <td>Microsoft Surface</td>    <td>799.99</td>  </tr></table></body></html>";
        assertEquals(expected, actual);
    }

    @Test
    void saveInventoryAsJSON() {
        File outFile = new File("outputFiles/JSONOutput.json");

        // Copy Inventory Items to a json file
        fileManager.saveInventoryAsJSON(outFile, itemModelTest);

        // Read in the Content of the file
        String actual = fileManager.readString(outFile);
        String expected = "[{\"sNumber\":\"ABCDE12345\",\"name\":\"MacBook Pro\",\"price\":1199.0},{\"sNumber\":\"QWERTY7890\",\"name\":\"AirPods Pro\",\"price\":199.99},{\"sNumber\":\"NIKE123456\",\"name\":\"Air Max\",\"price\":170.0},{\"sNumber\":\"ABCDE34567\",\"name\":\"Microsoft Surface\",\"price\":799.99}]";
        assertEquals(expected, actual);
    }
}