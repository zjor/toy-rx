package com.github.zjor.signals.spreadsheet;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class SpreadSheetComponent {

    private List<List<CellComponent>> cells = new LinkedList<>();
    private SpreadSheet sheet = new SpreadSheet();

    private int rows;
    private int cols;

    private void initCells() {
        for (int r = 0; r < rows; r++) {
            List<CellComponent> row = new LinkedList<>();
            for (int c = 0; c < cols; c++) {
                String name = getCellName(r + 1, c);
                CellComponent cell = new CellComponent(sheet.createCell(name));
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
            List<CellComponent> row = cells.get(r);
            for (int c = 0; c < row.size(); c++) {
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.weightx = 0.2;
                constraints.gridx = c + 1;
                constraints.gridy = r + 1;
                constraints.fill = GridBagConstraints.HORIZONTAL;
                container.add(row.get(c).getComponent(), constraints);
            }
        }

        return container;
    }

    private class CellComponent {
        private JTextField exprControl;
        private JLabel valueControl;

        public CellComponent(Cell model) {
            exprControl = new JTextField("0.0");
            valueControl = new JLabel("0.0");
            exprControl.addActionListener(e -> {
                cells.stream().flatMap(l -> l.stream()).forEach(c -> c.valueControl.setForeground(Color.BLACK));
                model.getExprStream().next(exprControl.getText());
                valueControl.setForeground(Color.RED);
            });
            model.getValueStream().subscribe(value -> {
                valueControl.setText(value);
                valueControl.setForeground(Color.GREEN);
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
