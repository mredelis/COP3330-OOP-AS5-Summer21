package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileManager {

    /* METHODS TO LOAD A TSV, HTML OR JSON FILE */

    // Load TSV.txt file
    public List<Item> loadInventoryAsTSV(File file) {

        List<Item> tempItemList = new ArrayList<>();
        String[] fields;
        Double priceField = null;

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                fields = scanner.nextLine().split("\t");

                try {
                    priceField = Double.parseDouble(fields[2]);
                } catch (NumberFormatException e) {
                    System.out.println(fields[2] + "cannot be parsed into a Double");
                    e.printStackTrace();
                }

                // Create an Item and add the newly created Item to a List of Items
                tempItemList.add(new Item(fields[0], fields[1], priceField));
            }
        } catch (FileNotFoundException e) {
            System.out.println(file + " was not found.");
            e.printStackTrace();
        }

        return tempItemList;
    }

    // Load HTML file
    public List<Item> loadInventoryAsHTML(File file) {
        // Read html to String
        String htmlString = readString(file);

        // Read html to Document
        Document htmlDoc = Jsoup.parse(htmlString);

        Element table = htmlDoc.selectFirst("table");
        Elements rows = null;
        if (table != null) {
            rows = table.select("tr");
        }

        Item tempItem;
        List<Item> tempItemList = new ArrayList<>();
        String[] fields = new String[3];
        Double tmpPrice = null;

        if (rows != null) {
            for (int i = 1; i < rows.size(); i++) { // skip first header row
                // Get each Row
                Element row = rows.get(i);
                Elements cols = row.select("td");
                for (int j = 0; j < cols.size(); j++) {
                    // Get each Column of a Row
                    Element col = cols.get(j);
                    fields[j] = col.text();
                }

                // Add try/catch for any errors parsing the Item Price into Double
                try {
                    tmpPrice = Double.parseDouble(fields[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Cannot parse into Double the String read from html file corresponding to price.");
                    e.printStackTrace();
                }

                // Create a new Item
                tempItem = new Item(fields[0], fields[1], tmpPrice);

                // Add Item to List
                tempItemList.add(tempItem);
            }
        }
        return tempItemList;
    }

    // Load JSON file
    public List<Item> loadInventoryAsJSON(File file) {

        // Read json to String
        String jsonString = readString(file);

        Type jsonItemType = new TypeToken<ArrayList<JsonItem>>() {
        }.getType();

        List<JsonItem> jsonItems = new Gson().fromJson(jsonString, jsonItemType);

        // Convert List to Observable
        List<Item> temp = new ArrayList<>();
        for (int i = 0; i < jsonItems.size(); i++) {
            temp.add(new Item(jsonItems.get(i).getsNumber(), jsonItems.get(i).getName(), jsonItems.get(i).getPrice()));
        }

        return temp;
    }

    /* METHODS TO AID LOAD FILES */

    // Return an HTML or JSON string read from the file
    public String readString(File file) {

        // Read html file to String
        StringBuilder str = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                str.append(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str.toString();
    }

    /* METHODS TO SAVE A TSV, HTML OR JSON FILE */

    public void saveInventoryAsTSV(File file, ItemModel itemModel) {
        // Create TSV String
        String tsvString = buildTSVString(itemModel);

        // Call writer function
        writeToFile(file, tsvString);
    }

    public void saveInventoryAsHTML(File file, ItemModel itemModel) {
        // Create html String
        String htmlString = buildHTMLString(itemModel);

        // Call write function
        writeToFile(file, htmlString);
    }

    public void saveInventoryAsJSON(File file, ItemModel itemModel) {
        // Create json String
        String jsonString = buildJSONString(itemModel);

        // Call write function
        writeToFile(file, jsonString);
    }


    /* METHODS TO AID SAVE FILES */

    public String buildTSVString(ItemModel itemModel) {
        StringBuilder buffer = new StringBuilder();

        // Write one inventory Item per line and separate each field with a /t character
        for (int i = 0; i < itemModel.getItems().size(); i++) {
            buffer.append(itemModel.getItems().get(i).convertItemToTSVString());
            // New line for each Item
            buffer.append(System.lineSeparator());
        }
        return buffer.toString();
    }

    public String buildHTMLString(ItemModel itemModel) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("""
            <!DOCTYPE html>
            <html>
            <head>
              <title>OOP Assignment 5</title><style>
            table {
              font-family: arial, sans-serif;
              border-collapse: collapse;
              width: 40%;
            }

            td, th {
              border: 1px solid #dddddd;
              text-align: left;
              padding: 8px;
            }
            </style>
            </head>
            <body>
            <h2>Inventory Tracker</h2>
            <table>
              <tr>
                <th>Serial Number</th>
                <th>Name</th>
                <th>Value in dollars</th>
              </tr>
            """);
        for (int i = 0; i < itemModel.getItems().size(); i++) {
            buffer.append("  <tr>\n    <td>")
                .append(itemModel.getItems().get(i).getSerialNumber())
                .append("</td>\n    <td>")
                .append(itemModel.getItems().get(i).getName())
                .append("</td>\n    <td>")
                .append(itemModel.getItems().get(i).getValue())
                .append("</td>\n  </tr>\n");
        }
        buffer.append("""
            </table>
            </body>
            </html>""");

        return buffer.toString();
    }

    public String buildJSONString(ItemModel itemModel) {
        // Custom decoder to make the Observables work with Gson

        List<JsonItem> items = new ArrayList<>();
        for (int i = 0; i < itemModel.getItems().size(); i++) {
            items.add(new JsonItem(itemModel.getItems().get(i).getSerialNumber(), itemModel.getItems().get(i).getName(), itemModel.getItems().get(i).getValue()));
        }
//        String json = new Gson().toJson(items);
        return new Gson().toJson(items);
    }


    // Write a given string to a file
    public void writeToFile(File file, String string) {
        try {
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(file));
            outWriter.write(string);
            outWriter.close();

        } catch (IOException e) {
            System.out.println("A writing error has occurred.");
            e.printStackTrace();
        }
    }

}
