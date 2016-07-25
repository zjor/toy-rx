package com.github.zjor.signals.spreadsheet;

import com.github.zjor.signals.Var;
import com.github.zjor.signals.spreadsheet.engine.Binary;
import com.github.zjor.signals.spreadsheet.engine.Expr;
import com.github.zjor.signals.spreadsheet.engine.LiteralExpr;
import com.github.zjor.signals.spreadsheet.engine.RefExpr;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpreadSheetComponent {

    private List<List<Cell>> cells = new LinkedList<>();
    private Map<String, Cell> cellsMap = new HashMap<>();

    private int rows;
    private int cols;

    private void initCells() {
        for (int r = 0; r < rows; r++) {
            List<Cell> row = new LinkedList<>();
            for (int c = 0; c < cols; c++) {
                String cellName = getCellName(r + 1, c);
                Cell cell = new Cell();
                cellsMap.put(cellName, cell);
                row.add(cell);
            }
            cells.add(row);
        }
    }

    public SpreadSheetComponent(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        initCells();
    }

    private void createColsHeader(Container container) {
        for (int c = 1; c < cols + 1; c++) {
            JLabel label = new JLabel("" + (char) ('A' + c - 1));
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = c;
            constraints.gridy = 0;
            container.add(label, constraints);
        }
    }

    private void createRowsHeader(Container container) {
        for (int r = 1; r < rows + 1; r++) {
            JLabel label = new JLabel("" + r);
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = r;
            container.add(label, constraints);
        }
    }

    private String getCellName(int row, int col) {
        return "" + (char) ('A' + col) + row;

    }

    public Container render() {
        JPanel container = new JPanel(new GridBagLayout());
        createColsHeader(container);
        createRowsHeader(container);

        for (int r = 0; r < cells.size(); r++) {
            List<Cell> row = cells.get(r);
            for (int c = 0; c < row.size(); c++) {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.weightx = 0.2;
                constraints.gridx = c + 1;
                constraints.gridy = r + 1;
                constraints.fill = GridBagConstraints.HORIZONTAL;

                Cell cell = row.get(c);
                container.add(cell.getComponent(), constraints);
            }
        }

        return container;
    }

    private Expr parse(String expression) {
        if (expression.matches("[-+]?[0-9]*\\.?[0-9]*")) {
            return new LiteralExpr(Double.parseDouble(expression));
        }

        if (expression.matches("[A-Za-z][0-9]")) {
            if (!cellsMap.containsKey(expression)) {
                throw new IllegalArgumentException(expression + " cell is not present");
            }
            return new RefExpr(cellsMap.get(expression).var);
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

    private class Cell {
        private JTextField exprControl;
        private JLabel valueControl;

        private Var<Double> var;

        public Cell() {
            exprControl = new JTextField("0.0");
            valueControl = new JLabel("0.0");
            var = new Var(() -> 0.0);

            exprControl.addActionListener(e -> {
                var.update(() -> {
                    double value = parse(exprControl.getText()).eval();
                    valueControl.setText("" + value);
                    return value;
                });
            });
        }

        public Component getComponent() {
            JPanel pane = new JPanel(new BorderLayout());
            pane.add(exprControl, BorderLayout.CENTER);
            pane.add(valueControl, BorderLayout.EAST);
            return pane;
        }
    }
}
