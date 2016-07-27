package com.github.zjor.ui.util;

import javax.swing.*;
import java.awt.*;

public class CenteredWindow extends JFrame {

    public CenteredWindow(String title, int width, int height) throws HeadlessException {
        super(title);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
