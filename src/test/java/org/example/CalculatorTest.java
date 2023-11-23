package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import org.example.Calculator.Number;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CalculatorTest {

    @BeforeEach
    void setUp() {
        Calculator.expressions = new ArrayList<>();
        Calculator.read_idx = 0;
    }

    @Test
    void testAddition() {
        Calculator.parse("3 + 2");
        Number result = Calculator.solve();
        assertEquals(new Number(5), result, "3 + 2 should equal 5");

        Calculator.parse("3+ 2");
        result = Calculator.solve();
        assertEquals(new Number(5), result, "3+ 2 should equal 5");

    }

    @Test
    void testSubtraction() {
        Calculator.parse("3 - 2");
        Number result = Calculator.solve();
        assertEquals(new Number(1), result, "3 - 2 should equal 1");

        Calculator.parse("3 + -2");
        result = Calculator.solve();
        assertEquals(new Number(1), result, "3 + -2 should equal 1");
    }

    @Test
    void testMultiplication() {
        Calculator.parse("3 * 2");
        Number result = Calculator.solve();
        assertEquals(new Number(6), result, "3 * 2 should equal 6");

        Calculator.parse("3*2");
        result = Calculator.solve();
        assertEquals(new Number(6), result, "3*2 should equal 6");
    }

    @Test
    void testDivision() {
        Calculator.parse("6 / 2");
        Number result = Calculator.solve();
        assertEquals(new Number(3), result, "6 / 2 should equal 3");

        Calculator.parse("6  /  2");
        result = Calculator.solve();
        assertEquals(new Number(3), result, "6  /  2 should equal 3");
    }

    @Test
    void testParentheses() {
        Calculator.parse("(3 + 2) * 2");
        Number result = Calculator.solve();
        assertEquals(new Number(10), result, "(3 + 2) * 2 should equal 10");

        Calculator.parse("2 * (3 + 2)");
        result = Calculator.solve();
        assertEquals(new Number(10), result, "2 * (3 + 2) should equal 10");
    }

    @Test
    void testInvalidExpression() {
        assertThrows(InvalidParameterException.class, () -> {
            Calculator.parse("3 +");
        }, "Expression '3 +' should throw InvalidParameterException");
    }

    @Test
    void testInvalidCharacters() {
        assertThrows(InvalidParameterException.class, () -> {
            Calculator.parse("3 + a");
        }, "Expression '3 + a' should throw InvalidParameterException");
    }

    @Test
    void testMismatchedParentheses() {
        assertThrows(InvalidParameterException.class, () -> {
            Calculator.parse("(3 + 2");
        }, "Expression '(3 + 2' should throw InvalidParameterException");
    }

    // その他のテストケースを追加することができます。
}
