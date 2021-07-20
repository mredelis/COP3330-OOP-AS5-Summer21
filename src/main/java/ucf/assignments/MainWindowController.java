package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private final ItemModel itemModel;

    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private TableView<Item> tableView;
    @FXML private TableColumn<Item, String> itemSerialNumberColumn;
    @FXML private TableColumn<Item, String> itemNameColumn;
    @FXML private TableColumn<Item, Double> itemValueColumn;
    @FXML private TextField itemSerialNumberTextField;
    @FXML private TextField itemNameTextField;
    @FXML private TextField itemValueTextField;
    @FXML private Button addNewItemButton;
    @FXML private Button deleteSelectedItemButton;
    @FXML private Button updateSelectedItemButton;
    @FXML private Label serialNumErrorLabel;
    @FXML private Label nameErrorLabel;
    @FXML private Label priceErrorLabel;


    public MainWindowController() {
        this.itemModel = new ItemModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // set up table columns
        itemSerialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        itemValueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        // Allow for the table fields to be editable
        tableView.setEditable(true);
        itemSerialNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        itemNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        itemValueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        // load dummy data for testing
        try {
            itemModel.getItems().addAll(getTestItems());
            tableView.setItems(itemModel.getItems());

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        if(serialNumText.isEmpty() || nameText.isEmpty() || itemValueTextField.getText().isEmpty()) {
            addItemInvalidInputAlert("All fields must be filled in to add a new item.");
//            itemSerialNumberTextField.clear();
//            itemNameTextField.clear();
//            itemValueTextField.clear();
            validInput = false;
        }
        else {
            if (nameText.length() < 2 || nameText.length() > 256) {
                nameErrorLabel.setVisible(true);
                nameErrorLabel.setText("Item name must be between 2 and 256 characters.");
//                addItemInvalidInputAlert("Item name must be between 2 and 256 characters.");
                itemNameTextField.clear();
                validInput = false;
            }

            if(!(serialNumText.matches("[0-9A-Z]+") && serialNumText.length() == 10)){
//                addItemInvalidInputAlert("Serial number can be either a letter or digit of length 10.");
//                itemSerialNumberTextField.setPromptText("Serial number can be either a letter or digit of length 10.");
                serialNumErrorLabel.setVisible(true);
                serialNumErrorLabel.setText("Invalid Serial Number.");
                itemSerialNumberTextField.clear();
                validInput = false;
            } else {
                if(containsSerialNumber(serialNumText)) {
                    serialNumErrorLabel.setVisible(true);
                    serialNumErrorLabel.setText("Serial number already exists.");
                    itemSerialNumberTextField.clear();
                    validInput = false;
                }
            }


            try {
                price = Double.parseDouble(itemValueTextField.getText());
            } catch (NumberFormatException e) {
//                addItemInvalidInputAlert("Enter a valid price for the item.");
                priceErrorLabel.setVisible(true);
                priceErrorLabel.setText("Invalid price.");
                itemValueTextField.clear();
                validInput = false;
            }
        }

        if(validInput){
            addItem(serialNumText, nameText, price);

            itemSerialNumberTextField.setPromptText("Item Serial Number");
            itemSerialNumberTextField.clear();

            itemNameTextField.setPromptText("Item Name");
            itemNameTextField.clear();

            itemValueTextField.setPromptText("Item Price");
            itemValueTextField.clear();
        }
    }


    private void addItemInvalidInputAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Alert");
        alert.setHeaderText("Item cannot be added");
        alert.setContentText(contentText);
        alert.showAndWait();
    }


    public void addItem(String serialNumber, String name, Double price) {
        itemModel.getItems().add(new Item(serialNumber, name, price));
    }

    private boolean containsSerialNumber(String serialNumText){
        for(int i = 0; i < itemModel.getItems().size(); i++){
            if(itemModel.getItems().get(i).getSerialNumber().equalsIgnoreCase(serialNumText)){
                return true;
            }
        }

        return false;
    }


    // Method to return an ObservableList of Inventory Item Objects
    public ObservableList<Item> getTestItems() {
        ObservableList<Item> items = FXCollections.observableArrayList();
        items.add(new Item("AXB124AXYE","Xbox One", 399.00));
        items.add(new Item("S40AZBDE4E","Samsung TV", 599.99));
        items.add(new Item("ABCDE12345","iPad Pro", 999.99));
        items.add(new Item("ABX4H321SW","Microsoft Surface", 499.99));

        return items;
    }



}
