package ucf.assignments;

import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Edelis Molina
 */

class InputValidatorTest {

    @Test
    @DisplayName("Item name validation")
    void isValidNameTest() {
        InputValidator inputValidator = new InputValidator();

        // Empty string. Invalid string
        String name1 = " ";
        // 300 characters string. Invalid string
        String name2 = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. " +
            "Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. " +
            "Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec.";
        // Valid string
        String name3 = "MacBook Pro";

        assertFalse(inputValidator.isValidName(name1));
        assertFalse(inputValidator.isValidName(name2));
        assertTrue(inputValidator.isValidName(name3));
    }

    @Test
    @DisplayName("Item Serial Number Validation")
    void isValidSerialNumberTest() {
        InputValidator inputValidator = new InputValidator();

        // Empty string. Invalid Serial #
        String serial1 = " ";
        // Invalid length for Serial #
        String serial2 = "ABCDEF";
        // Valid Serial #
        String serial3 = "ABCDEF1234";

        assertFalse(inputValidator.isValidSerialNumber(serial1));
        assertFalse(inputValidator.isValidSerialNumber(serial2));
        assertTrue(inputValidator.isValidSerialNumber(serial3));
    }

    @Test
    void containsSerialNumberTest() {

    }
}