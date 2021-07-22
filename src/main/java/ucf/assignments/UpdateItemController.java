package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateItemController implements Initializable {

    private ItemModel itemModel;
    private Item selectedItem;

    @FXML private TextField updateSerialNumberTextField;
    @FXML private TextField updateNameTextField;
    @FXML private TextField updateValueTextField;
    @FXML private Button updateItemButton;
    @FXML private Label updateSerialNumErrorLabel;
    @FXML private Label updateNameErrorLabel;
    @FXML private Label updatePriceErrorLabel;

    public UpdateItemController(ItemModel itemModel, Item selectedItem) {
        this.itemModel = itemModel;
        this.selectedItem = selectedItem;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO
        updateSerialNumberTextField.setText(selectedItem.getSerialNumber());
        updateNameTextField.setText(selectedItem.getName());
        updateValueTextField.setText(Double.toString(selectedItem.getValue()));
    }




}


