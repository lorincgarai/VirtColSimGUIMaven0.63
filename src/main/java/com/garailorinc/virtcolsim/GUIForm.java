package com.garailorinc.virtcolsim;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by garail on 2018. 12. 06..
 */
public class GUIForm  extends JFrame {
    private JPanel mainPanel;
    JTextArea outPutTextArea;
    private JButton startButton;
    private JLabel p0LLabel;
    public  JTextField p0LTextField;
    private JLabel p0MLabel;
    public JTextField p0MTextField;
    private JLabel p0SLabel;
    public JTextField p0StextField1;
    private JLabel gamutPointRedLabel;
    private JLabel gamutPointGreenLabel;
    private JLabel gamutPointBlueLabel;
    public JTextField gamutPointRedxTextField;
    public JTextField gamutPointBluexTextField;
    public JTextField gamutPointGreenxTextField;
    public JTextField gamutPointRedyTextField;
    public JTextField gamutPointGreenyTextField;
    public JTextField gamutPointBlueyTextField;
    public JTextField equilTimeTextField;
    private JLabel equilTimeLabel;
    private JLabel dtLabel;
    public JTextField dtTextField1;
    private JLabel tauRedLabel;
    private JLabel tauGreenLabel;
    private JLabel tauBlueLabel;
    public JTextField tauRedTextField;
    public JTextField tauGreenTextField;
    public JTextField tauBlueTextField1;
    private JLabel pathLabel;
    JTextField pathTextField1;
    private JLabel e0Label;
    public JTextField enulltextField1;
    public JTextField eszorzoTextField;
    private JLabel DLabel;
    public JTextField dTextField1;
    JCheckBox createTxtFileCheckBox;
    JCheckBox createExcelFileCheckBox;
    JTextField deltacMinTextField;
    JRadioButton matrixExcelRadioButton;
    private JRadioButton simpleListExcelRadioButton;
    public JCheckBox calculateCorrelationCheckBox;


    private GUIForm(String title)
    {
        super(title);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        Path path = FileSystems.getDefault().getPath("").toAbsolutePath();
        pathTextField1.setText(path.toString());
        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
            start();

            }
        });
    }


    private void start()
    {

        Simulate simulateTask = new Simulate(this);
        simulateTask.execute();

    }


    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUIForm("VirtcolsimGUI v0.63 (Excel&Txt&Ranking)");
            }
        });

    }
}

