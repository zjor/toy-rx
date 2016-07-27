package com.github.zjor.signals.spreadsheet;

import com.github.zjor.ui.util.CenteredWindow;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class SpreadSheetApp extends CenteredWindow {

    private static final Color GREEN_COLOR = new Color(0, 150, 0);

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

    public SpreadSheetApp(int rows, int cols) {
        super("Reactive SpreadSheet Example", 800, 600);
        this.rows = rows;
        this.cols = cols;
        initCells();
        getContentPane().add(render(), BorderLayout.CENTER);
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
                constraints.insets = new Insets(3, 3, 3, 3);
                container.add(row.get(c).getComponent(), constraints);
            }
        }

        return container;
    }

    private class CellComponent {
        private JTextField exprControl;
        private JLabel valueControl;

        public CellComponent(Cell model) {
            exprControl = new JTextField();
            exprControl.setMinimumSize(new Dimension(128, 32));
            exprControl.setPreferredSize(new Dimension(128, 32));

            valueControl = new JLabel();
            valueControl.setMinimumSize(new Dimension(64, 32));
            valueControl.setPreferredSize(new Dimension(64, 32));

            exprControl.addActionListener(e -> {
                cells.stream().flatMap(l -> l.stream()).forEach(c -> c.valueControl.setForeground(Color.BLACK));
                model.getExprStream().next(exprControl.getText());
                valueControl.setForeground(Color.RED);
            });
            model.getValueStream().subscribe(value -> {
                valueControl.setText(value.orElse("NaN"));
                valueControl.setForeground(GREEN_COLOR);
            });
        }

        public Component getComponent() {
            JPanel pane = new JPanel(new BorderLayout());
            pane.add(exprControl, BorderLayout.CENTER);
            pane.add(valueControl, BorderLayout.EAST);
            pane.setBorder(BorderFactory.createLoweredSoftBevelBorder());


            return pane;
        }
    }

    public static void main(String[] args) {
        SpreadSheetApp app = new SpreadSheetApp(5, 4);
        app.setVisible(true);
    }
}
