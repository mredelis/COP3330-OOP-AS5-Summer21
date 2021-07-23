package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainWindowController implements Initializable {

    private ItemModel itemModel;
    private FileChooser fileChooser;

    @FXML
    private TextField searchTextField;
    @FXML
    private Button searchButton;
    @FXML
    private TableView<Item> tableView;
    @FXML
    private TableColumn<Item, String> itemSerialNumberColumn;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, Double> itemValueColumn;
    @FXML
    private TextField itemSerialNumberTextField;
    @FXML
    private TextField itemNameTextField;
    @FXML
    private TextField itemValueTextField;
    @FXML
    private Button addNewItemButton;
    @FXML
    private Button deleteSelectedItemButton;
    @FXML
    private Button updateSelectedItemButton;
    @FXML
    private Label serialNumErrorLabel;
    @FXML
    private Label nameErrorLabel;
    @FXML
    private Label priceErrorLabel;
    @FXML
    private MenuBar menuBar;


    public MainWindowController() {
        this.itemModel = new ItemModel();
        this.fileChooser = new FileChooser();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set up table columns
        itemSerialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Allow for the table fields to be editable
//        tableView.setEditable(true);
//        itemSerialNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        itemNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        itemValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        itemValueColumn.setCellFactory(tc -> new TableCell<Item, Double>() {
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

        // Bind Delete & Update buttons to selection of the table. Delete and Update buttons will be disable is no row is selected
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

        searchTextField.textProperty().addListener((observable, oldValue, newValue) ->
            tableView.setItems(filterList(newValue))
        );

    }


    @FXML
    void addNewItemButtonClicked(ActionEvent event) {
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
    void deleteSelectedItemButtonClicked(ActionEvent event) {
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
    void menuItemQuitClicked(ActionEvent event) {
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
    void menuItemGetHelpClicked(ActionEvent event) {
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
    void updateSelectedItemButtonClicked(ActionEvent event) throws IOException {
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


    // Search ---------------------------------------------------------------------------
    public ObservableList<Item> filterList(String searchText) {
        List<Item> filteredList = new ArrayList<>();

        for (int i = 0; i < itemModel.getItems().size(); i++) {
            if (searchFindsItem(itemModel.getItems().get(i), searchText)) {
                filteredList.add(itemModel.getItems().get(i));
            }
        }
        return FXCollections.observableArrayList(filteredList);
    }


    public boolean searchFindsItem(Item item, String searchText) {
        return (item.getName().toLowerCase().contains(searchText.toLowerCase()) ||
            item.getSerialNumber().toLowerCase().contains(searchText.toLowerCase()));
    }
    // End of Search ---------------------------------------------------------------------------


    // Save as ---------------------------------------------------------------------------------
    @FXML
    void menuItemSaveAsClicked(ActionEvent event) {

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

        // For testing. Delete before final submission
        System.out.println(file);
        System.out.println(fileName);
        System.out.println(fileExtension);

        if (file != null) {
            switch (fileExtension) {
                case "txt" -> saveInventoryAsTSV(file);
                case "html" -> saveInventoryAsHTML(file);
                case "json" -> saveInventoryAsJSON(file);
            }

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
        buffer.append(" <!DOCTYPE html>\n" +
            "<html>\n" +
            "<style type=\"text/css\">\n" +
            ".tg  {border-collapse:collapse;border-spacing:0;}\n" +
            ".tg td{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;\n" +
            "  overflow:hidden;padding:10px 10px;word-break:normal;}\n" +
            ".tg th{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;\n" +
            "  font-weight:normal;overflow:hidden;padding:10px 10px;word-break:normal;}\n" +
            ".tg .tg-0lax{text-align:left;vertical-align:top}\n" +
            "</style>\n" +
            "<head>\n" +
            "<title>Assignment 5</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "<h1>Inventory Tracker</h1>\n" +
            "<table class=\"tg\">\n" +
            "<thead>\n" +
            "  <tr>\n" +
            "   <th class=\"tg-0lax\">Serial Number</th>\n" +
            "   <th class=\"tg-0lax\">Name<br></th>\n" +
            "   <th class=\"tg-0lax\">Value</th>\n" +
            "  </tr>\n" +
            "</thead>\n" +
            "<tbody>\n");
        for (int i = 0; i < itemModel.getItems().size(); i++) {
            buffer.append("  <tr>\n    <td class=\"tg-0lax\">")
                .append(itemModel.getItems().get(i).getSerialNumber())
                .append("</td>\n    <td class=\"tg-0lax\">")
                .append(itemModel.getItems().get(i).getName())
                .append("</td>\n    <td class=\"tg-0lax\">")
                .append(itemModel.getItems().get(i).getValue())
                .append("</td>\n  <tr>\n");
        }
        buffer.append(
            "</tbody>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html> ");

        String html = buffer.toString();

        return html;
    }


    public void saveInventoryAsJSON(File file) {

    }

    // End of Save as ---------------------------------------------------------------------------------
    //
    //
    //
    //
    // Load as ---------------------------------------------------------------------------------
    @FXML
    void menuItemOpenFileClicked(ActionEvent event) {

        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text (Tab delimited)", "*.txt"),
            new FileChooser.ExtensionFilter("Web Page", "*.html"),
            new FileChooser.ExtensionFilter("JSON (JavaScript Object Notation)", "*.json"));


        File file = fileChooser.showOpenDialog(new Stage());
        // save the chosen directory for next time
        fileChooser.setInitialDirectory(file.getParentFile());

        String fileName = file.getName();
        String fileExtension = FilenameUtils.getExtension(fileName);

        // For testing. Delete before final submission
        System.out.println(file);
        System.out.println(fileName);
        System.out.println(fileExtension);

        if (file != null) {
            // Remove previous Items from ItemModel
            itemModel.getItems().clear();

            List<Item> loadedList = new ArrayList<>();

            switch (fileExtension) {
                case "txt" -> loadedList = loadInventoryAsTSV(file);
                case "html" -> loadedList = loadInventoryAsHTML(file);
                case "json" -> loadedList = loadInventoryAsJSON(file);
            }

            // Add loaded Items from the file in the table
            itemModel.getItems().addAll(FXCollections.observableArrayList(loadedList));
        }
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

        return null;
    }

    public List<Item> loadInventoryAsJSON(File file) {

        return null;
    }


}
