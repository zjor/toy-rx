package com.github.zjor.signals.spreadsheet;

import com.github.zjor.signals.Signal;
import com.github.zjor.signals.Var;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpreadSheet {

    private Map<String, Cell> cells = new HashMap<>();

    private void init() {
        cells.put("a1", new Cell("0", new Var<>(() -> 0.0)));
        cells.put("b1", new Cell("0", new Var<>(() -> 0.0)));
        cells.put("a2", new Cell("0", new Var<>(() -> 0.0)));
        cells.put("b2", new Cell("0", new Var<>(() -> 0.0)));
    }

    public void update(String cell, String expression) {
        cells.get(cell).update(expression);
    }

    private Expr parse(String expression) {
        if (expression.matches("[-+]?[0-9]*\\.?[0-9]*")) {
            return new LiteralExpr(Double.parseDouble(expression));
        }

        if (expression.matches("[A-Za-z][0-9]")) {
            //TODO: check nonexistence
            return new RefExpr(cells.get(expression).var);
        }

        if (expression.matches("([A-Za-z][0-9])([+-/\\\\*])([A-Za-z][0-9])")) {
            Matcher m = Pattern.compile("([A-Za-z][0-9])([+-/\\\\*])([A-Za-z][0-9])").matcher(expression);
            if (m.find()) {
                String left = m.group(1);
                String op = m.group(2);
                String right = m.group(3);
                return new Binary(parse(left), parse(right), op.charAt(0));
            }
        }
        throw new IllegalArgumentException("Failed to parse expression: " + expression);
    }

    class Cell {

        private String expr;

        private Var<Double> var;

        public Cell(String expr, Var<Double> var) {
            this.expr = expr;
            this.var = var;
        }

        private void update(String expr) {
            this.expr = expr;
            var.update(() -> {
                double value = parse(expr).eval();
                System.out.println("Recalculated value: " + value);
                return value;
            });


        }
    }

    interface Expr {

        Double eval();

    }

    class LiteralExpr implements Expr {
        private double value;

        public LiteralExpr(double value) {
            this.value = value;
        }

        @Override
        public Double eval() {
            return value;
        }
    }

    class RefExpr implements Expr {

        private Signal<Double> signal;

        public RefExpr(Signal<Double> signal) {
            this.signal = signal;
        }

        @Override
        public Double eval() {
            return signal.getValue();
        }
    }

    class Binary implements Expr {

        private Expr left;
        private Expr right;
        private char operator;

        public Binary(Expr left, Expr right, char operator) {
            this.left = left;
            this.right = right;
            this.operator = operator;
        }

        @Override
        public Double eval() {
            switch (operator) {
                case '+': return left.eval() + right.eval();
                case '-': return left.eval() - right.eval();
                case '*': return left.eval() * right.eval();
                case '/': return left.eval() / right.eval();
                default:
                    throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        }
    }

    public static void main(String[] args) {
        SpreadSheet sheet = new SpreadSheet();
        sheet.init();
        sheet.update("a1", "1");
        sheet.update("b1", "2");
        sheet.update("a2", "a1+b1");
        sheet.update("b2", "a2+b1");

        sheet.update("a1", "3");
    }

}
