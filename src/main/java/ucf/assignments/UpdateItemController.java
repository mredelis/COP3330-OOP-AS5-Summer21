package ucf.assignments;

/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
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

    @FXML
    void updateItemButtonClicked(ActionEvent event) {
        updateSerialNumErrorLabel.setVisible(false);
        updateNameErrorLabel.setVisible(false);
        updatePriceErrorLabel.setVisible(false);

        boolean validInput = true;

        String serialNumText = updateSerialNumberTextField.getText().toUpperCase();
        String nameText = updateNameTextField.getText();
        Double price = null;

        if (serialNumText.isEmpty() || nameText.isEmpty() || updateValueTextField.getText().isEmpty()) {
            // Alert the user to enter information in all text fields
            errorMessage("All fields must be filled in to update an item.");

            validInput = false;

        } else {
            if (nameText.length() < 2 || nameText.length() > 256) {
                updateNameErrorLabel.setVisible(true);
                updateNameErrorLabel.setText("Item name must be between 2 and 256 characters.");
                updateNameTextField.clear();
                validInput = false;
            }

            if (!(serialNumText.matches("[0-9A-Z]+") && serialNumText.length() == 10)) {
                updateSerialNumErrorLabel.setVisible(true);
                updateSerialNumErrorLabel.setText("Invalid Serial Number.");
                updateSerialNumberTextField.clear();
                validInput = false;
            } else if (containsSerialNumber(serialNumText)) {
                errorMessage("Serial number already exists.");
                updateSerialNumberTextField.clear();
                validInput = false;
            }

            try {
                price = Double.parseDouble(updateValueTextField.getText());
            } catch (NumberFormatException e) {
                updatePriceErrorLabel.setVisible(true);
                updatePriceErrorLabel.setText("Invalid price.");
                updateValueTextField.clear();
                validInput = false;
            }
        }

        if (validInput) {
            editItem(serialNumText, nameText, price);

            System.out.println("This is the modified list");
            for(int i = 0; i < itemModel.getItems().size(); i++) {
                System.out.print(itemModel.getItems().get(i).getSerialNumber() + " " + itemModel.getItems().get(i).getName() + " " + itemModel.getItems().get(i).getValue());
                System.out.println();
            }

            // Change scene to tableView
            Node node = (Node) event.getSource();
            Stage thisStage = (Stage) node.getScene().getWindow();
            thisStage.close();
        }
    }


    public void editItem(String serialNumber, String name, Double price) {
        selectedItem.setSerialNumber(serialNumber);
        selectedItem.setName(name);
        selectedItem.setValue(price);
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
        alert.setHeaderText("Item cannot be updated");
        alert.setContentText(contentText);
        alert.showAndWait();
    }




}


