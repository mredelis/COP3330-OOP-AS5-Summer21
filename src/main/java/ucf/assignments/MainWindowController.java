package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class MainWindowController implements Initializable {

    private ItemModel itemModel;
    private Set<String> serialNumSet;

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


    public MainWindowController() {
        this.itemModel = new ItemModel();
        serialNumSet = new HashSet<>();
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


    // Method to return an ObservableList of Inventory Item Objects
    public ObservableList<Item> getTestItems() {
        ObservableList<Item> items = FXCollections.observableArrayList();
        items.add(new Item("Xbox One", "AXB124AXYE", 399.00));
        items.add(new Item("Samsung TV", "S40AZBDE4E", 599.99));
        items.add(new Item("iPad Pro", "ABCDE12345", 999.99));
        items.add(new Item("Microsoft Surface", "QWERTY321", 499.99));

        return items;
    }


}
