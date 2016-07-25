package com.github.zjor.signals.spreadsheet;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    private static final int WIDTH = 900;
    private static final int HEIGHT = 600;

    public App() throws HeadlessException {
        super("Reactive SpreadSheet Example");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2, WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        SpreadSheetComponent sheet = new SpreadSheetComponent(4, 5);


        getContentPane().add(sheet.render(), BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        (new App()).setVisible(true);
    }
}
