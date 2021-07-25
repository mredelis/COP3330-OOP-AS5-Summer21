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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainWindowController implements Initializable {

    private ItemModel itemModel;
    private FileChooser fileChooser;
    private FileManager fileManager;
    private InputValidator inputValidator;

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
        this.fileManager = new FileManager();
        this.inputValidator = new InputValidator();
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
            // if not a valid name, meaning length is not between 2 and 256 characters inclusive
            if(!inputValidator.isValidName(nameText)) {
                nameErrorLabel.setVisible(true);
                nameErrorLabel.setText("Item name must be between 2 and 256 characters.");
                itemNameTextField.clear();
                validInput = false;
            }

            if (!inputValidator.isValidSerialNumber(serialNumText)) {
                serialNumErrorLabel.setVisible(true);
                serialNumErrorLabel.setText("Invalid Serial Number.");
                itemSerialNumberTextField.clear();
                validInput = false;

            } else if (inputValidator.containsSerialNumber(itemModel, serialNumText)) {
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

    @FXML
    void clearSearchButtonClicked(ActionEvent event) {
        searchTextField.setText("");
        event.consume();
    }


    /* SAVE A TSV, HTML OR JSON FILES */
    @FXML
    void menuItemSaveAsClicked() {

        fileChooser.setTitle("Save As");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text (Tab delimited)", "*.txt"),
            new FileChooser.ExtensionFilter("Web Page", "*.html"),
            new FileChooser.ExtensionFilter("JSON (JavaScript Object Notation)", "*.json"));

        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            // save the chosen directory for next time
            fileChooser.setInitialDirectory(file.getParentFile());

            String fileName = file.getName();
            String fileExtension = FilenameUtils.getExtension(fileName);

            switch (fileExtension) {
                case "txt":
                    fileManager.saveInventoryAsTSV(file, itemModel);
                    break;
                case "html":
                    fileManager.saveInventoryAsHTML(file, itemModel);
                    break;
                case "json":
                    fileManager.saveInventoryAsJSON(file, itemModel);
                    break;
            }
        }

    }


    /* LOAD A TSV, HTML OR JSON FILES */
    @FXML
    void menuItemOpenFileClicked() {

        fileChooser.setTitle("Open");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text (Tab delimited)", "*.txt"),
            new FileChooser.ExtensionFilter("Web Page", "*.html"),
            new FileChooser.ExtensionFilter("JSON (JavaScript Object Notation)", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(new Stage());
//        System.out.println(file);

        if (selectedFile != null) {
            // save the chosen directory for next time
            fileChooser.setInitialDirectory(selectedFile.getParentFile());

            String fileName = selectedFile.getName();
            String fileExtension = FilenameUtils.getExtension(fileName);

            // Remove previous Items from ItemModel
            itemModel.getItems().clear();

            List<Item> loadedList = new ArrayList<>();

            switch (fileExtension) {
                case "txt":
                    loadedList = fileManager.loadInventoryAsTSV(selectedFile);
                    break;
                case "html":
                    loadedList = fileManager.loadInventoryAsHTML(selectedFile);
                    break;
                case "json":
                    loadedList = fileManager.loadInventoryAsJSON(selectedFile);
                    break;
            }

            // Add loaded Items from the file into the table
            itemModel.getItems().addAll(FXCollections.observableArrayList(loadedList));
        }
    }

    @FXML
    void menuItemGetHelpClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Get Help");
        alert.setHeaderText("Refer to file README.md in the GitHub Repository for the project");
        alert.showAndWait();
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

    // Method to return an ObservableList of Inventory Item Objects
    public ObservableList<Item> getTestItems() {
        ObservableList<Item> items = FXCollections.observableArrayList();
        items.add(new Item("AXB124AXYE", "Xbox One", 399.00));
        items.add(new Item("S40AZBDE4E", "Samsung TV", 599.99));
        items.add(new Item("ABCDE12345", "iPad Pro", 999.99));
        items.add(new Item("ABX4H321SW", "Microsoft Surface", 499.9945));

        return items;
    }
}
