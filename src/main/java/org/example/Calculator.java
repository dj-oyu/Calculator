package org.example;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;


public class Calculator {

    static final boolean NUM = false;
    static final boolean OP = true;
    static final String msg = """
            半角数字、四則演算記号(+-*/)、丸かっこ、空白文字のみを含む式を入力してね！
            (終了する場合は何も入力せずEnter)
            """;

    static final String paren_error = "丸かっこを見直してね";
    static final String invalid_expr_error = "変な式だね";
    static List<Term> expressions;
    static int read_idx;

    enum OpSymbol {
        ADD, SUB, MUL, DIV
    }

    enum Parenthesis {
        LEFT, RIGHT
    }

    sealed interface Term permits Number, Op, Paren {
    }

    record Number(int numerator, int denominator) implements Term {
        Number(int n) {
            this(n, 1);
        }

        Number(int numerator, int denominator) {
            assert (denominator != 0);

            if (denominator < 0) {
                numerator = -numerator;
                denominator = -denominator;
            }
            if (numerator == 0) {
                this.numerator = 0;
                this.denominator = denominator;
            } else {
                var g = gcd(numerator, denominator);
                this.numerator = numerator / g;
                this.denominator = denominator / g;
            }
        }

        Number add(Number other) {
            var g = gcd(this.denominator, other.denominator);
            var num = this.numerator * other.denominator / g + other.numerator * this.denominator / g;
            return new Number(num, this.denominator * other.denominator / g);
        }

        Number sub(Number other) {
            var g = gcd(this.denominator, other.denominator);
            var num = this.numerator * other.denominator / g - other.numerator * this.denominator / g;
            return new Number(num, this.denominator * other.denominator / g);
        }

        Number mul(Number other) {
            return new Number(this.numerator * other.numerator, this.denominator * other.denominator);
        }

        Number div(Number other) {
            return new Number(this.numerator * other.denominator, this.denominator * other.numerator);
        }

        @Override
        public String toString() {
            if (denominator == 1) {
                return String.format("%d", numerator);
            }
            return String.format("%.8f", 1.0 * numerator / denominator);
        }

        int gcd(int a, int b) {
            a = Math.abs(a);
            if (a < b) {
                int tmp = a;
                a = b;
                b = tmp;
            }
            do {
                int tmp = a;
                a = b;
                b = tmp % b;
            } while (b > 0);
            return a;
        }
    }

    record Op(OpSymbol op) implements Term {
    }

    record Paren(Parenthesis side) implements Term {
    }

    public static void main(String[] args) {
        System.out.println("こんにちは！電卓くんです！");

        try (var sc = new Scanner(System.in)) {
            while (true) {
                expressions = new ArrayList<>();
                read_idx = 0;

                System.out.println(msg);

                String input = sc.nextLine();
                if (input.isEmpty())
                    return;

                try {
                    parse(input);
                    System.out.printf("答え: %s\n", solve());

                } catch (NumberFormatException ex) {
                    System.out.println("不正な式が入力されました");
                } catch (InvalidParameterException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void parse(String input) {
        boolean state = NUM;
        int paren_pair = 0;
        var tmp_num = new StringBuilder();

        Consumer<OpSymbol> addExpr = op -> {
            if (!tmp_num.isEmpty())
                expressions.add(new Number(Integer.parseInt(tmp_num.toString())));
            expressions.add(new Op(op));
            tmp_num.setLength(0);
        };
        Consumer<Void> addNum = Void -> {
            if (!tmp_num.isEmpty())
                expressions.add(new Number(Integer.parseInt(tmp_num.toString())));
            tmp_num.setLength(0);
        };

        for (int idx = 0; idx < input.length(); idx++) {
            char c = input.charAt(idx);
            switch (c) {
                case ' ', '_' -> {
                    /* do nothing */
                }
                case '+' -> {
                    addExpr.accept(OpSymbol.ADD);
                    state = NUM;
                }
                case '-' -> {
                    if (state == OP) {
                        addExpr.accept(OpSymbol.SUB);
                        state = NUM;
                    } else {
                        tmp_num.append(c);
                    }
                }
                case '*' -> {
                    addExpr.accept(OpSymbol.MUL);
                    state = NUM;
                }
                case '/' -> {
                    addExpr.accept(OpSymbol.DIV);
                    state = NUM;
                }
                case '(' -> {
                    paren_pair++;
                    expressions.add(new Paren(Parenthesis.LEFT));
                    state = NUM;
                }
                case ')' -> {
                    paren_pair--;
                    addNum.accept(null);
                    expressions.add(new Paren(Parenthesis.RIGHT));
                    state = OP;
                }
                case '.' -> throw new InvalidParameterException("小数は未対応です。");
                default -> {
                    if (Character.isDigit(c)) {
                        tmp_num.append(c);
                        state = OP;
                    } else {
                        throw new InvalidParameterException(msg);
                    }
                }
            }
        }
        if (paren_pair != 0)
            throw new InvalidParameterException(paren_error);
        if (state == NUM)
            throw new InvalidParameterException(invalid_expr_error);

        addNum.accept(null);
    }

    /**
     * expr := add_sub [("+"|"-") add_sub]*
     * add_sub := mul_div [("*"|"/") mul_div]*
     * mul_div := DIGITS | \( expr \)
     */
    static Number solve() {
        return calc_add_sub();
    }

    static Number calc_add_sub() {
        var current = calc_mul_div();
        if (read_idx == expressions.size())
            return current;
        return switch (expressions.get(read_idx++)) {
            case Op(OpSymbol op) when op == OpSymbol.ADD -> current.add(calc_add_sub());
            case Op(OpSymbol op) when op == OpSymbol.SUB -> current.sub(calc_add_sub());
            case Paren p when p.side == Parenthesis.RIGHT -> current;
            default -> {
                read_idx--;
                yield current;
            }
        };
    }

    static Number calc_mul_div() {
        var current = calc_number();
        if (read_idx == expressions.size())
            return current;
        return switch (expressions.get(read_idx++)) {
            case Op(OpSymbol op) when op == OpSymbol.MUL -> current.mul(calc_number());
            case Op(OpSymbol op) when op == OpSymbol.DIV -> current.div(calc_number());
            case Paren p when p.side == Parenthesis.RIGHT -> current;
            default -> {
                read_idx--;
                yield current;
            }
        };
    }

    static Number calc_number() {
        return switch (expressions.get(read_idx++)) {
            case Paren ignored -> solve();
            case Number number -> number;
            case Op ignored -> throw new InvalidParameterException(invalid_expr_error);
        };
    }
}
