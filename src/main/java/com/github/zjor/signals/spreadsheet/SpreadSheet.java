package com.github.zjor.signals.spreadsheet;

import com.github.zjor.signals.spreadsheet.engine.Binary;
import com.github.zjor.signals.spreadsheet.engine.Expr;
import com.github.zjor.signals.spreadsheet.engine.LiteralExpr;
import com.github.zjor.signals.spreadsheet.engine.RefExpr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpreadSheet {

    private Map<String, Cell> cells = new HashMap<>();

    private Function<String, Double> calculator = expr -> parse(expr).eval();

    private void init() {
        String[] names = new String[] {
                "a1", "b1", "a2", "b2"
        };
        Arrays.asList(names).forEach(c -> {
            Cell cell = new Cell(calculator);
            cells.put(c, cell);
            cell.getValues().subscribe(value -> System.out.println(value));
        });
    }

    public void update(String cell, String expression) {
        cells.get(cell).update(expression);
    }

    private Expr parse(String expression) {
        if (expression.matches("[-+]?[0-9]*\\.?[0-9]*")) {
            return new LiteralExpr(Double.parseDouble(expression));
        }

        if (expression.matches("[A-Za-z][0-9]")) {
            expression = expression.toLowerCase();
            if (!cells.containsKey(expression)) {
                throw new IllegalArgumentException("Cell " + expression + " doesn't exist");
            }
            return new RefExpr(cells.get(expression).getSignal());
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

    public static void main(String[] args) {
        SpreadSheet sheet = new SpreadSheet();
        sheet.init();
        sheet.update("a1", "1");
        sheet.update("b1", "a1");
        sheet.update("a1", "2");

    }

}
