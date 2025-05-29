package com.example.dailymate;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.dailymate.Presentation.ui.AddEditActivity;
import com.example.dailymate.utils.inputValidator;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testInputValidation_validInput_returnsTrue() {
        assertTrue(inputValidator.isValidInput("Meeting", "Discuss plans"));
    }

    @Test
    public void testInputValidation_emptyTitle_returnsFalse() {
        assertFalse(inputValidator.isValidInput("", "Some description"));
    }

    @Test
    public void testInputValidation_nullDescription_returnsFalse() {
        assertFalse(inputValidator.isValidInput("Task", null));
    }
}