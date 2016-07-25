package com.github.zjor.signals.spreadsheet;

import com.github.zjor.signals.Var;
import com.github.zjor.signals.spreadsheet.engine.Binary;
import com.github.zjor.signals.spreadsheet.engine.Expr;
import com.github.zjor.signals.spreadsheet.engine.LiteralExpr;
import com.github.zjor.signals.spreadsheet.engine.RefExpr;

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

    public static void main(String[] args) {
        SpreadSheet sheet = new SpreadSheet();
        sheet.init();
        sheet.update("a1", "1");
        sheet.update("b1", "a1");
        sheet.update("a1", "2");

    }

}
