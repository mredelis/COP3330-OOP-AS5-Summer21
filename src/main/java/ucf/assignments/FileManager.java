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


    public List<Item> loadInventoryAsHTML(File file) {
        // Read html to String
        String htmlString = readString(file);

        // Read html to Document
        Document htmlDoc = Jsoup.parse(htmlString);

        Element table = htmlDoc.selectFirst("table");
        Elements rows = table.select("tr");

        Item tempItem;
        List<Item> tempItemList = new ArrayList<>();
        String[] fields = new String[3];
        Double tmpPrice = null;

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

        return tempItemList;
    }


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

}
