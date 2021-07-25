package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

public class MainWindowController implements Initializable {

    private ItemModel itemModel;
    private FileChooser fileChooser;
    private FileManager fileManager;

    @FXML private TextField searchTextField;
    @FXML private TableView<Item> tableView;
    @FXML private TableColumn<Item, String> itemSerialNumberColumn;
    @FXML private TableColumn<Item, String> itemNameColumn;
    @FXML private TableColumn<Item, Double> itemValueColumn;
    @FXML private TextField itemSerialNumberTextField;
    @FXML private TextField itemNameTextField;
    @FXML private TextField itemValueTextField;
    @FXML private Button deleteSelectedItemButton;
    @FXML private Button updateSelectedItemButton;
    @FXML private Label serialNumErrorLabel;
    @FXML private Label nameErrorLabel;
    @FXML private Label priceErrorLabel;
    @FXML private MenuBar menuBar;


    public MainWindowController() {
        this.itemModel = new ItemModel();
        this.fileChooser = new FileChooser();
        this.fileChooser = new FileChooser();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set up table columns
        itemSerialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Display formatted item price in tableView
        itemValueColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", value.floatValue()));
                }
            }
        });

        // Bind Delete & Update Buttons to selection of the table. Delete and Update Buttons will be disable is no row is selected
        deleteSelectedItemButton.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));
        updateSelectedItemButton.disableProperty().bind(Bindings.isEmpty(tableView.getSelectionModel().getSelectedItems()));

        // load dummy data for testing
        try {
            itemModel.getItems().addAll(getTestItems());
            tableView.setItems(itemModel.getItems());

        } catch (Exception e) {
            tableView.setItems(null);
            e.printStackTrace();
        }

        // Defining a method inside the ChangeListener of the searchTextField to update the search every time the user
        // enters or edits the search
        // newValue is the text currently in the searchTextField
         searchTextField.textProperty().addListener((observable, oldValue, newValue) ->
            tableView.setItems(filterList(newValue))
        );

    }

    @FXML
    void clearSearchButtonClicked(ActionEvent event) {
        searchTextField.setText("");
        event.consume();
    }


    @FXML
    void addNewItemButtonClicked() {
        serialNumErrorLabel.setVisible(false);
        nameErrorLabel.setVisible(false);
        priceErrorLabel.setVisible(false);

        boolean validInput = true;

        String serialNumText = itemSerialNumberTextField.getText().toUpperCase();
        String nameText = itemNameTextField.getText();
        Double price = null;

        if (serialNumText.isEmpty() || nameText.isEmpty() || itemValueTextField.getText().isEmpty()) {
            // Alert the user to enter information in all text fields
            errorMessage("All fields must be filled in to add a new item.");

            validInput = false;

        } else {
            if (nameText.length() < 2 || nameText.length() > 256) {
                nameErrorLabel.setVisible(true);
                nameErrorLabel.setText("Item name must be between 2 and 256 characters.");
                itemNameTextField.clear();
                validInput = false;
            }

            if (!(serialNumText.matches("[0-9A-Z]+") && serialNumText.length() == 10)) {
                serialNumErrorLabel.setVisible(true);
                serialNumErrorLabel.setText("Invalid Serial Number.");
                itemSerialNumberTextField.clear();
                validInput = false;
            } else if (containsSerialNumber(serialNumText)) {
                errorMessage("Serial number already exists.");
                itemSerialNumberTextField.clear();
                validInput = false;
            }

            try {
                price = Double.parseDouble(itemValueTextField.getText());

            } catch (NumberFormatException e) {
                priceErrorLabel.setVisible(true);
                priceErrorLabel.setText("Invalid price.");
                itemValueTextField.clear();
                validInput = false;
            }
        }

        if (validInput) {

            Item item = addItem(serialNumText, nameText, price);
            itemModel.getItems().add(item);

            itemSerialNumberTextField.setPromptText("Item Serial Number");
            itemSerialNumberTextField.clear();

            itemNameTextField.setPromptText("Item Name");
            itemNameTextField.clear();

            itemValueTextField.setPromptText("Item Price");
            itemValueTextField.clear();
        }
    }


    public Item addItem(String serialNumber, String name, Double price) {
        return new Item(serialNumber, name, price);
    }

    public boolean containsSerialNumber(String serialNumText) {
        for (int i = 0; i < itemModel.getItems().size(); i++) {
            if (itemModel.getItems().get(i).getSerialNumber().equalsIgnoreCase(serialNumText)) {
                return true;
            }
        }
        return false;
    }

    private void errorMessage(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Alert");
        alert.setHeaderText("Item cannot be added");
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML
    void deleteSelectedItemButtonClicked() {
        if (!itemModel.getItems().isEmpty()) {
            Item selectedItem = tableView.getSelectionModel().getSelectedItem();
            deleteItem(selectedItem);
            tableView.getSelectionModel().clearSelection();
        }
    }

    public void deleteItem(Item selectedItem) {
        itemModel.getItems().remove(selectedItem);
    }


    @FXML
    void menuItemQuitClicked() {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        Stage stage = (Stage) menuBar.getScene().getWindow();
        exitAlert.setTitle("Exit Application");
        exitAlert.setHeaderText("Are you sure you want to exit?");
        exitAlert.initModality(Modality.APPLICATION_MODAL);
        exitAlert.initOwner(stage);
        exitAlert.showAndWait();

        if (exitAlert.getResult() == ButtonType.OK) {
            Platform.exit();
        } else {
            exitAlert.close();
        }
    }

    @FXML
    void menuItemGetHelpClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Get Help");
        alert.setHeaderText("Refer to file README.md in the GitHub Repository for the project");
        alert.showAndWait();
    }


    // Method to return an ObservableList of Inventory Item Objects
    public ObservableList<Item> getTestItems() {
        ObservableList<Item> items = FXCollections.observableArrayList();
        items.add(new Item("AXB124AXYE", "Xbox One", 399.00));
        items.add(new Item("S40AZBDE4E", "Samsung TV", 599.99));
        items.add(new Item("ABCDE12345", "iPad Pro", 999.99));
        items.add(new Item("ABX4H321SW", "Microsoft Surface", 499.9945));

        return items;
    }

    @FXML
    void updateSelectedItemButtonClicked() throws IOException {
        // Get selected Item
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();
        int index = tableView.getSelectionModel().getSelectedIndex();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateItem.fxml"));

        // Create a controller instance
        UpdateItemController controller = new UpdateItemController(itemModel, selectedItem);
        // Set in the FXML loader
        loader.setController(controller);

        Parent updateItemParent = loader.load();

        // Set the scene
        Scene updateItemScene = new Scene(updateItemParent);

        Stage secondaryStage = new Stage();
        secondaryStage.setScene(updateItemScene);
        secondaryStage.setTitle("Update Item");
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.showAndWait();

        tableView.getItems().set(index, selectedItem);

    }


    /* METHODS TO AID SEARCH */

    // Loop through an ObservableList<Item> and create a new ObservableList<Item> with the items that match the searchText
    public ObservableList<Item> filterList(String searchText) {
        List<Item> filteredList = new ArrayList<>();

        for (int i = 0; i < itemModel.getItems().size(); i++) {
            if (searchFindsItem(itemModel.getItems().get(i), searchText)) {
                filteredList.add(itemModel.getItems().get(i));
            }
        }
        return FXCollections.observableArrayList(filteredList);
    }


    // Return if an Item's Name or Serial Number contains the searchText
    public boolean searchFindsItem(Item item, String searchText) {
        return (item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
            item.getSerialNumber().toLowerCase().contains(searchText.toLowerCase()));
    }



    // Save as ---------------------------------------------------------------------------------
    @FXML
    void menuItemSaveAsClicked() {

        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text (Tab delimited)", "*.txt"),
            new FileChooser.ExtensionFilter("Web Page", "*.html"),
            new FileChooser.ExtensionFilter("JSON (JavaScript Object Notation)", "*.json"));

        File file = fileChooser.showSaveDialog(new Stage());
        // save the chosen directory for next time
        fileChooser.setInitialDirectory(file.getParentFile());

        String fileName = file.getName();
        String fileExtension = FilenameUtils.getExtension(fileName);

        switch (fileExtension) {
            case "txt":
                saveInventoryAsTSV(file);
                break;
            case "html":
                saveInventoryAsHTML(file);
                break;
            case "json":
                saveInventoryAsJSON(file);
                break;
        }
    }


    public void saveInventoryAsTSV(File file) {

        try {
            // create a writer
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(file));

            // Write one inventory Item per line and separate each field with a /t character
            for (int i = 0; i < itemModel.getItems().size(); i++) {
                outWriter.write(itemModel.getItems().get(i).convertItemToTSVString());
                // New line for each Item
                outWriter.newLine();
            }

            outWriter.close();

        } catch (IOException e) {
            System.out.println("A writing error has occurred.");
            e.printStackTrace();
        }
    }


    public void saveInventoryAsHTML(File file) {
        // Create html String
        String htmlString = buildHTMLString();
        // create a writer
        try {
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(file));
            outWriter.write(htmlString);

            outWriter.close();
        } catch (IOException e) {
            System.out.println("A writing error has occurred.");
            e.printStackTrace();
        }
    }

    public String buildHTMLString() {
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


    public void saveInventoryAsJSON(File file) {
        String jsonString = getJSONString();

        // create a writer
        try {
            BufferedWriter outWriter = new BufferedWriter(new FileWriter(file));
            outWriter.write(jsonString);
            outWriter.close();

        } catch (IOException e) {
            System.out.println("A writing error has occurred.");
            e.printStackTrace();
        }
    }

    public String getJSONString() {
        // Custom decoder to make the Observables work with Gson

        List<JsonItem> items = new ArrayList<>();
        for(int i = 0; i < itemModel.getItems().size(); i++) {
            items.add(new JsonItem(itemModel.getItems().get(i).getSerialNumber(), itemModel.getItems().get(i).getName(), itemModel.getItems().get(i).getValue()));
        }

//        String json = new Gson().toJson(items);

        return new Gson().toJson(items);
    }

    // End of Save as ---------------------------------------------------------------------------------
    //
    //
    //
    //
    // Load as ---------------------------------------------------------------------------------
    @FXML
    void menuItemOpenFileClicked() {

        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text (Tab delimited)", "*.txt"),
            new FileChooser.ExtensionFilter("Web Page", "*.html"),
            new FileChooser.ExtensionFilter("JSON (JavaScript Object Notation)", "*.json"));

        File file = fileChooser.showOpenDialog(new Stage());
        System.out.println(file);
        // save the chosen directory for next time
        fileChooser.setInitialDirectory(file.getParentFile());

        String fileName = file.getName();
        System.out.println(fileName);

        String fileExtension = FilenameUtils.getExtension(fileName);
        System.out.println(fileExtension);

        // Remove previous Items from ItemModel
        itemModel.getItems().clear();

        List<Item> loadedList = new ArrayList<>();

        switch (fileExtension) {
            case "txt":
                loadedList = loadInventoryAsTSV(file);
                break;
            case "html":
                loadedList = loadInventoryAsHTML(file);
                break;
            case "json":
                loadedList = loadInventoryAsJSON(file);
                break;
        }

        // Add loaded Items from the file in the table
        itemModel.getItems().addAll(FXCollections.observableArrayList(loadedList));
    }


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
        String htmlString = readHTMLString(file);

        // Read html to Document
        Document htmlDoc = Jsoup.parse(htmlString);

        Element table = htmlDoc.selectFirst("table");
        Elements rows = table.select("tr");

        Item tempItem;
        List<Item> tempItemList = new ArrayList<>();
        String[] fields = new String[3];
        Double tmpPrice = null;

        for(int i = 1; i < rows.size(); i++) { // skip first header row
            // Get each Row
            Element row = rows.get(i);
            Elements cols = row.select("td");
            for(int j = 0; j < cols.size(); j++) {
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



    public String readHTMLString(File file) {
        // Read html file to String
        StringBuilder htmlStr = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                htmlStr.append(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return htmlStr.toString();
    }


    public List<Item> loadInventoryAsJSON(File file) {

        // Read json to String
        String jsonString = readJSONtoString(file);

        Type jsonItemType = new TypeToken<ArrayList<JsonItem>>(){}.getType();

        List<JsonItem> jsonItems = new Gson().fromJson(jsonString, jsonItemType);

        // Convert List to Observable
        List<Item> temp = new ArrayList<>();
        for(int i = 0; i < jsonItems.size(); i++){
            temp.add(new Item(jsonItems.get(i).getsNumber(), jsonItems.get(i).getName(), jsonItems.get(i).getPrice()));
        }

        return temp;
    }

    public String readJSONtoString(File file) {
        // Read html file to String
        StringBuilder jsonStr = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                jsonStr.append(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonStr.toString();
    }
}
