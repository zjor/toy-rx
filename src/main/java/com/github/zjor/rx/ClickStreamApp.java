package com.github.zjor.rx;

import javax.swing.*;
import java.awt.*;

public class ClickStreamApp extends JFrame {

    private static final int WIDTH = 400;
    private static final int HEIGHT = 200;

    public ClickStreamApp() throws HeadlessException {
        super("Click Stream Example");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - WIDTH) / 2, (screenSize.height - HEIGHT) / 2, WIDTH, HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JButton button = new JButton("Click me!");
        JLabel label = new JLabel("Clicks count: 0");
        getContentPane().add(button, BorderLayout.NORTH);
        getContentPane().add(label, BorderLayout.SOUTH);

        Observable<Integer> clickStream = new Observable<>();
        button.addActionListener(e -> clickStream.next(1));
        clickStream.fold(0, (a, b) -> a + b).subscribe(n -> label.setText("Clicks count: " + n));
    }

    public static void main(String[] args) {
        new ClickStreamApp().setVisible(true);


    }
}
