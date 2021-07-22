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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    private ItemModel itemModel;

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
    @FXML private MenuBar menuBar;


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
            addItem(serialNumText, nameText, price);

            itemSerialNumberTextField.setPromptText("Item Serial Number");
            itemSerialNumberTextField.clear();

            itemNameTextField.setPromptText("Item Name");
            itemNameTextField.clear();

            itemValueTextField.setPromptText("Item Price");
            itemValueTextField.clear();
        }
    }


    public void addItem(String serialNumber, String name, Double price) {
        itemModel.getItems().add(new Item(serialNumber, name, price));
    }

    public boolean containsSerialNumber(String serialNumText){
        for(int i = 0; i < itemModel.getItems().size(); i++){
            if(itemModel.getItems().get(i).getSerialNumber().equalsIgnoreCase(serialNumText)){
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
        if(!itemModel.getItems().isEmpty()){
            Item selectedItem = tableView.getSelectionModel().getSelectedItem();
            deleteItem(selectedItem);
            tableView.getSelectionModel().clearSelection();
        }
    }

    public void deleteItem(Item selectedItem){
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
        items.add(new Item("AXB124AXYE","Xbox One", 399.00));
        items.add(new Item("S40AZBDE4E","Samsung TV", 599.99));
        items.add(new Item("ABCDE12345","iPad Pro", 999.99));
        items.add(new Item("ABX4H321SW","Microsoft Surface", 499.9945));

        return items;
    }

    @FXML
    void updateSelectedItemButtonClicked(ActionEvent event) throws IOException {
        // Equivalent to Parent tableViewParent = FXMLLoader(getClass().getResource("UpdateItem.fxml"))
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateItem.fxml"));





        // Get selected Item
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();

        // Create a controller instance
        UpdateItemController controller = new UpdateItemController(itemModel, selectedItem);

        // Set in the FXML loader
        loader.setController(controller);

        Parent tableViewParent = loader.load();

        // Set the scene
        Scene tableViewScene = new Scene(tableViewParent);

        //This line gets the Stage information
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(tableViewScene);
        window.show();

    }


}
