package com.garailorinc.virtcolsim;

import javax.swing.*;
import java.awt.*;

/**
 * Created by garail on 2018. 12. 13..
 */
public class SIMForm extends JFrame {
    private JPanel simPanel;
    private JTextArea textArea1;

    private SIMForm(String title) {
        super(title);
        setContentPane(simPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public static void create() {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SIMForm("window: VirtcolsimGUI v0.1");
            }
        });
    }
}