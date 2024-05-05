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
    void testAddition1() {
        Calculator.parse("3 + 2");
        Number result = Calculator.solve();
        assertEquals(new Number(5), result, "3 + 2 should equal 5");
    }

    @Test
    void testAddition2() {
        Calculator.parse("3+ 5");
        Number result = Calculator.solve();
        assertEquals(new Number(8), result, "3+ 5 should equal 8");
    }

    @Test
    void testSubtraction1() {
        Calculator.parse("3 - 2");
        Number result = Calculator.solve();
        assertEquals(new Number(1), result, "3 - 2 should equal 1");
    }

    @Test
    void testSubtraction2() {
        Calculator.parse("4 + -2");
        Number result = Calculator.solve();
        assertEquals(new Number(2), result, "4 + -2 should equal 2");
    }

    @Test
    void testMultiplication1() {
        Calculator.parse("3 * 2");
        Number result = Calculator.solve();
        assertEquals(new Number(6), result, "3 * 2 should equal 6");
    }

    @Test
    void testMultiplication2() {
        Calculator.parse("3*7");
        Number result = Calculator.solve();
        assertEquals(new Number(21), result, "3*7 should equal 21");
    }

    @Test
    void testDivision1() {
        Calculator.parse("6 / 2");
        Number result = Calculator.solve();
        assertEquals(new Number(3), result, "6 / 2 should equal 3");
    }

    @Test
    void testDivision2() {
        Calculator.parse("10  /  5");
        Number result = Calculator.solve();
        assertEquals(new Number(2), result, "10  /  5 should equal 2");
    }

    @Test
    void testParentheses1() {
        Calculator.parse("(3 + 2) * 2");
        Number result = Calculator.solve();
        assertEquals(new Number(10), result, "(3 + 2) * 2 should equal 10");
    }

    @Test
    void testParentheses2() {
        Calculator.parse("2 * (3 + 2)");
        Number result = Calculator.solve();
        assertEquals(new Number(10), result, "2 * (3 + 2) should equal 10");
    }

    @Test
    void testParentheses3() {
        Calculator.parse("(3 + 2) * (1 + 1)");
        Number result = Calculator.solve();
        assertEquals(new Number(10), result, "(3 + 2) * (1 + 1) should equal 10");
    }

    @Test
    void testNestedParentheses() {
        Calculator.parse("((3 + 2) * 3 + 1) * -1");
        Number result = Calculator.solve();
        assertEquals(new Number(-16), result, "((3 + 2) * 3 + 1) * -1 should equal -16");
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
