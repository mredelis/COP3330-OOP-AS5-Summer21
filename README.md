# University of Central Florida
## COP3330: Object Oriented Programming, Summer 2021

# Overview
Using IntelliJ and Gradle, you will create a GUI-based desktop application to allow a user to track their personal inventory.

The program should allow you to enter an item, a serial number, and estimated value. The program should then be able to display a tabular report of the data that looks like this:

| Value          | Serial Number  |   Name       |
|----------------|----------------|--------------|
| $399.00        | AXB124AXY3     | Xbox One     |
| $599.99        | S40AZBDE47     | Samsung TV   |


The program should also allow the user to export the data as either a tab-separated value (TSV) file, or as a HTML file. When exported as an HTML file, the data should be stored inside of a table structure to mimic the displayed appearance.

You will be responsible for both the design (UML diagrams) and implementation (production and test code) of this application

# How to use the Application

**1. Add a new Inventory Item**
* Enter the Item Serial Number, Name, and Price in the Text Fields. 
* Click on the "Add New Item" button to add the new Item to the table.
* Before adding the item, the Application will check the following: 
1.  All fields must be filled in.
2.  Serial Number must be unique in the format of XXXXXXXXXX where X can be either a letter or digit.
3. 	Name must be between 2 and 256 characters 
4. Price is a valid number

**2. Remove an Item from the list**
* To enable the "Delete Selected Item" button an item from the table should be selected. 
* Once an item is selected, click "Delete Selected Item" button

**3. Edit an Inventory Item**
* To enable the "Edit Selected Item" button an item from the table should be selected. 
* Once an item is selected, click "Edit Selected Item" button and a new window will pop up with the selected item.
* Enter the new values and click on the "Update Item" button.
* Same validations 1 through 4 in "Add a new Inventory Item" above will be performed before updating an Item.

**4. Search for an Inventory Item**
* Enter the text to search in the Search Text Field and the table will populate the serial number and/or name of the items that meet the search criteria.
* Click the X button on the right side of the search bar to clear the search 

**5. Save Inventory Items as TSV, HTML or JSON**
* Go to Menu File and click Save As...
* When FileChooser prompts, select a location from the left pane to save the file and the format and type a name for the file.

**6. Load Inventory Items**
* Go to Menu File and click Open File...
* When FileChooser prompts, browse to the file location and select the file type

**7. Sort Inventory Items**
* The table can be sorted by Serial Number, Item Name and Item Price.
* Click on the corresponding column header to perform the sort

**8. Exit the Application**
* Go to File Menu and select Quit
